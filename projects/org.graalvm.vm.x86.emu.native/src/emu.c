/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
#define _GNU_SOURCE
#define _POSIX_C_SOURCE 200809L

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>

#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/signal.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <sys/user.h>
#include <signal.h>

#include <syscall.h>

#include <jni.h>
#include "org_graalvm_vm_x86_emu_Ptrace.h"

#define JNI_CHECK(x) \
	x; \
	if((*env)->ExceptionCheck(env)) { \
		return; \
	}

#define POSIX_CHECK(x) \
	if((x) == -1) { \
		throw_posix_exception(env, errno); \
		return; \
	}

#define POSIX_CHECK_Z(x) \
	if((x) == -1) { \
		throw_posix_exception(env, errno); \
		return 0; \
	}

static void throw_posix_exception(JNIEnv* env, int err)
{
	jclass cls = (*env)->FindClass(env, "org/graalvm/vm/posix/api/PosixException");
	jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
	jobject exc = (*env)->NewObject(env, cls, ctor, err);
	(*env)->Throw(env, exc);
}

static void throw_sigsegv(JNIEnv* env, void* addr)
{
	jclass cls = (*env)->FindClass(env, "org/graalvm/vm/memory/exception/SegmentationViolation");
	jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(J)V");
	jobject exc = (*env)->NewObject(env, cls, ctor, (long) addr);
	(*env)->Throw(env, exc);
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	return JNI_VERSION_1_4;
}

int wait_for_signal(JNIEnv* env, pid_t pid)
{
	int status;
	int options = 0;
	siginfo_t info;

	waitpid(pid, &status, options);

	POSIX_CHECK_Z(ptrace(PTRACE_GETSIGINFO, pid, NULL, &info));
	switch (info.si_signo) {
		case SIGSTOP:
			// ignore sigstop
			break;
		case SIGTRAP:
			switch(info.si_code) {
				case 0:
					case TRAP_BRKPT:
					// printf("Breakpoint? We didn't set one!\n");
					break;
				case TRAP_TRACE:
					// single step completed
					break;
				default:
					fprintf(stderr, "unknown SIGTRAP code: %d\n", info.si_code);
			}
			break;
		case SIGSEGV:
			fprintf(stderr, "SIGSEGV: %d\n", info.si_code);
			throw_sigsegv(env, info.si_addr);
			return 0;
		default:
			fprintf(stderr, "Got signal %s\n", strsignal(info.si_signo));
	}
	return !WIFEXITED(status);
}

JNIEXPORT jint JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_fork
  (JNIEnv* env, jclass self, jstring filename)
{
	const char* path = (*env)->GetStringUTFChars(env, filename, NULL);
	char* cmd = strdup(path);
	(*env)->ReleaseStringUTFChars(env, filename, path);

	pid_t pid = fork();
	if(pid == 0) {
		char* const args[] = {cmd, NULL};
		if(ptrace(PTRACE_TRACEME, 0, 0, 0) < 0) {
			abort();
		}
		kill(getpid(), SIGSTOP);
		// never reached
		execvp(args[0], args);
	} else if(pid == -1) {
		throw_posix_exception(env, errno);
	} else {
		wait_for_signal(env, pid);
		free(cmd);
		return pid;
	}
}

JNIEXPORT jint JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_waitForSignal
  (JNIEnv* env, jclass self, jint pid)
{
	return wait_for_signal(env, pid);
}

JNIEXPORT jlong JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_syscall
  (JNIEnv* env, jclass self, jint pid, jlong nr, jlong a1, jlong a2, jlong a3, jlong a4, jlong a5, jlong a6)
{
	struct user_regs_struct regs;
	struct user_regs_struct saved_regs;
	uint64_t insn;
	uint64_t rip;

	// patch current insn to syscall
	POSIX_CHECK_Z(ptrace(PTRACE_GETREGS, pid, NULL, &regs));
	saved_regs = regs;
	rip = regs.rip;
	insn = ptrace(PTRACE_PEEKDATA, pid, rip, NULL);
	POSIX_CHECK_Z(ptrace(PTRACE_POKEDATA, pid, rip, 0xCCCCCCCCCCCC050FL));

	// set regs
	regs.rax = nr;
	regs.rdi = a1;
	regs.rsi = a2;
	regs.rdx = a3;
	regs.r10 = a4;
	regs.r8 = a5;
	regs.r9 = a6;
	POSIX_CHECK_Z(ptrace(PTRACE_SETREGS, pid, NULL, &regs));

	// execute syscall
	POSIX_CHECK_Z(ptrace(PTRACE_SINGLESTEP, pid, NULL, NULL));
	wait_for_signal(env, pid);

	// get regs
	POSIX_CHECK_Z(ptrace(PTRACE_GETREGS, pid, NULL, &regs));

	// restore regs
	POSIX_CHECK_Z(ptrace(PTRACE_SETREGS, pid, NULL, &saved_regs));

	// restore insn
	POSIX_CHECK_Z(ptrace(PTRACE_POKEDATA, pid, rip, insn));

	return regs.rax;
}

JNIEXPORT jint JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_step
  (JNIEnv* env, jclass self, jint pid)
{
	// execute instruction
	POSIX_CHECK_Z(ptrace(PTRACE_SINGLESTEP, pid, NULL, NULL));
	return wait_for_signal(env, pid);
}

JNIEXPORT void JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_readRegisters
  (JNIEnv* env, jclass self, jint pid, jobject o)
{
	struct user_regs_struct regs;
	struct user_fpregs_struct fpregs;

	POSIX_CHECK(ptrace(PTRACE_GETREGS, pid, NULL, &regs));
	POSIX_CHECK(ptrace(PTRACE_GETFPREGS, pid, NULL, &fpregs));

	jclass clazz = (*env)->GetObjectClass(env, o);

	jfieldID rax = (*env)->GetFieldID(env, clazz, "rax", "J");
	(*env)->SetLongField(env, o, rax, regs.rax);

	jfieldID rbx = (*env)->GetFieldID(env, clazz, "rbx", "J");
	(*env)->SetLongField(env, o, rbx, regs.rbx);

	jfieldID rcx = (*env)->GetFieldID(env, clazz, "rcx", "J");
	(*env)->SetLongField(env, o, rcx, regs.rcx);

	jfieldID rdx = (*env)->GetFieldID(env, clazz, "rdx", "J");
	(*env)->SetLongField(env, o, rdx, regs.rdx);

	jfieldID rsi = (*env)->GetFieldID(env, clazz, "rsi", "J");
	(*env)->SetLongField(env, o, rsi, regs.rsi);

	jfieldID rdi = (*env)->GetFieldID(env, clazz, "rdi", "J");
	(*env)->SetLongField(env, o, rdi, regs.rdi);

	jfieldID rbp = (*env)->GetFieldID(env, clazz, "rbp", "J");
	(*env)->SetLongField(env, o, rbp, regs.rbp);

	jfieldID rsp = (*env)->GetFieldID(env, clazz, "rsp", "J");
	(*env)->SetLongField(env, o, rsp, regs.rsp);

	jfieldID r8 = (*env)->GetFieldID(env, clazz, "r8", "J");
	(*env)->SetLongField(env, o, r8, regs.r8);

	jfieldID r9 = (*env)->GetFieldID(env, clazz, "r9", "J");
	(*env)->SetLongField(env, o, r9, regs.r9);

	jfieldID r10 = (*env)->GetFieldID(env, clazz, "r10", "J");
	(*env)->SetLongField(env, o, r10, regs.r10);

	jfieldID r11 = (*env)->GetFieldID(env, clazz, "r11", "J");
	(*env)->SetLongField(env, o, r11, regs.r11);

	jfieldID r12 = (*env)->GetFieldID(env, clazz, "r12", "J");
	(*env)->SetLongField(env, o, r12, regs.r12);

	jfieldID r13 = (*env)->GetFieldID(env, clazz, "r13", "J");
	(*env)->SetLongField(env, o, r13, regs.r13);

	jfieldID r14 = (*env)->GetFieldID(env, clazz, "r14", "J");
	(*env)->SetLongField(env, o, r14, regs.r14);

	jfieldID r15 = (*env)->GetFieldID(env, clazz, "r15", "J");
	(*env)->SetLongField(env, o, r15, regs.r15);

	jfieldID rflags = (*env)->GetFieldID(env, clazz, "rflags", "J");
	(*env)->SetLongField(env, o, rflags, regs.eflags);

	jfieldID rip = (*env)->GetFieldID(env, clazz, "rip", "J");
	(*env)->SetLongField(env, o, rip, regs.rip);

	jfieldID fs_base = (*env)->GetFieldID(env, clazz, "fs_base", "J");
	(*env)->SetLongField(env, o, fs_base, regs.fs_base);

	jfieldID gs_base = (*env)->GetFieldID(env, clazz, "gs_base", "J");
	(*env)->SetLongField(env, o, gs_base, regs.gs_base);

	jfieldID mxcsr = (*env)->GetFieldID(env, clazz, "mxcsr", "J");
	(*env)->SetLongField(env, o, mxcsr, fpregs.mxcsr);

	jfieldID fcwd = (*env)->GetFieldID(env, clazz, "fcwd", "J");
	(*env)->SetLongField(env, o, fcwd, fpregs.cwd);

	jfieldID st_space = (*env)->GetFieldID(env, clazz, "st_space", "[B");
	jbyteArray sts = (*env)->GetObjectField(env, o, st_space);

	JNI_CHECK(jbyte* st_data = (*env)->GetPrimitiveArrayCritical(env, sts, NULL));
	if(!st_data) {
		jclass clazz = (*env)->FindClass(env, "java/lang/RuntimeException");
		(*env)->ThrowNew(env, clazz, "error while locking array");
		return;
	}

	memcpy(st_data, fpregs.st_space, 128);

	(*env)->ReleasePrimitiveArrayCritical(env, sts, st_data, 0);

	jfieldID xmm_space = (*env)->GetFieldID(env, clazz, "xmm_space", "[B");
	jbyteArray xmms = (*env)->GetObjectField(env, o, xmm_space);

	JNI_CHECK(jbyte* xmm_data = (*env)->GetPrimitiveArrayCritical(env, xmms, NULL));
	if(!xmm_data) {
		jclass clazz = (*env)->FindClass(env, "java/lang/RuntimeException");
		(*env)->ThrowNew(env, clazz, "error while locking array");
		return;
	}

	memcpy(xmm_data, fpregs.xmm_space, 256);

	(*env)->ReleasePrimitiveArrayCritical(env, xmms, xmm_data, 0);
}

JNIEXPORT void JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_writeRegisters
  (JNIEnv* env, jclass self, jint pid, jobject o)
{
	struct user_regs_struct regs;
	struct user_fpregs_struct fpregs;

	POSIX_CHECK(ptrace(PTRACE_GETREGS, pid, NULL, &regs));
	POSIX_CHECK(ptrace(PTRACE_GETFPREGS, pid, NULL, &fpregs));

	JNI_CHECK(jclass clazz = (*env)->GetObjectClass(env, o));

	jfieldID rax = (*env)->GetFieldID(env, clazz, "rax", "J");
	regs.rax = (*env)->GetLongField(env, o, rax);

	jfieldID rbx = (*env)->GetFieldID(env, clazz, "rbx", "J");
	regs.rbx = (*env)->GetLongField(env, o, rbx);

	jfieldID rcx = (*env)->GetFieldID(env, clazz, "rcx", "J");
	regs.rcx = (*env)->GetLongField(env, o, rcx);

	jfieldID rdx = (*env)->GetFieldID(env, clazz, "rdx", "J");
	regs.rdx = (*env)->GetLongField(env, o, rdx);

	jfieldID rsi = (*env)->GetFieldID(env, clazz, "rsi", "J");
	regs.rsi = (*env)->GetLongField(env, o, rsi);

	jfieldID rdi = (*env)->GetFieldID(env, clazz, "rdi", "J");
	regs.rdi = (*env)->GetLongField(env, o, rdi);

	jfieldID rbp = (*env)->GetFieldID(env, clazz, "rbp", "J");
	regs.rbp = (*env)->GetLongField(env, o, rbp);

	jfieldID rsp = (*env)->GetFieldID(env, clazz, "rsp", "J");
	regs.rsp = (*env)->GetLongField(env, o, rsp);

	jfieldID r8 = (*env)->GetFieldID(env, clazz, "r8", "J");
	regs.r8 = (*env)->GetLongField(env, o, r8);

	jfieldID r9 = (*env)->GetFieldID(env, clazz, "r9", "J");
	regs.r9 = (*env)->GetLongField(env, o, r9);

	jfieldID r10 = (*env)->GetFieldID(env, clazz, "r10", "J");
	regs.r10 = (*env)->GetLongField(env, o, r10);

	jfieldID r11 = (*env)->GetFieldID(env, clazz, "r11", "J");
	regs.r11 = (*env)->GetLongField(env, o, r11);

	jfieldID r12 = (*env)->GetFieldID(env, clazz, "r12", "J");
	regs.r12 = (*env)->GetLongField(env, o, r12);

	jfieldID r13 = (*env)->GetFieldID(env, clazz, "r13", "J");
	regs.r13 = (*env)->GetLongField(env, o, r13);

	jfieldID r14 = (*env)->GetFieldID(env, clazz, "r14", "J");
	regs.r14 = (*env)->GetLongField(env, o, r14);

	jfieldID r15 = (*env)->GetFieldID(env, clazz, "r15", "J");
	regs.r15 = (*env)->GetLongField(env, o, r15);

	jfieldID rflags = (*env)->GetFieldID(env, clazz, "rflags", "J");
	regs.eflags = (int) (*env)->GetLongField(env, o, rflags);

	jfieldID rip = (*env)->GetFieldID(env, clazz, "rip", "J");
	regs.rip = (*env)->GetLongField(env, o, rip);

	jfieldID fs_base = (*env)->GetFieldID(env, clazz, "fs_base", "J");
	regs.fs_base = (*env)->GetLongField(env, o, fs_base);

	jfieldID gs_base = (*env)->GetFieldID(env, clazz, "gs_base", "J");
	regs.gs_base = (*env)->GetLongField(env, o, gs_base);

	jfieldID mxcsr = (*env)->GetFieldID(env, clazz, "mxcsr", "J");
	fpregs.mxcsr = (*env)->GetLongField(env, o, mxcsr);

	jfieldID fcwd = (*env)->GetFieldID(env, clazz, "fcwd", "J");
	fpregs.cwd = (*env)->GetLongField(env, o, fcwd);

	jfieldID st_space = (*env)->GetFieldID(env, clazz, "st_space", "[B");
	jbyteArray sts = (*env)->GetObjectField(env, o, st_space);

	JNI_CHECK(jbyte* st_data = (*env)->GetPrimitiveArrayCritical(env, sts, NULL));
	if(!st_data) {
		jclass clazz = (*env)->FindClass(env, "java/lang/RuntimeException");
		(*env)->ThrowNew(env, clazz, "error while locking array");
		return;
	}

	memcpy(fpregs.st_space, st_data, 128);
	JNI_CHECK((*env)->ReleasePrimitiveArrayCritical(env, sts, st_data, 0));

	jfieldID xmm_space = (*env)->GetFieldID(env, clazz, "xmm_space", "[B");
	jbyteArray xmms = (*env)->GetObjectField(env, o, xmm_space);

	JNI_CHECK(jbyte* xmm_data = (*env)->GetPrimitiveArrayCritical(env, xmms, NULL));
	if(!xmm_data) {
		jclass clazz = (*env)->FindClass(env, "java/lang/RuntimeException");
		(*env)->ThrowNew(env, clazz, "error while locking array");
		return;
	}

	memcpy(fpregs.xmm_space, xmm_data, 256);
	JNI_CHECK((*env)->ReleasePrimitiveArrayCritical(env, xmms, xmm_data, 0));

	POSIX_CHECK(ptrace(PTRACE_SETFPREGS, pid, NULL, &fpregs));
	POSIX_CHECK(ptrace(PTRACE_SETREGS, pid, NULL, &regs));
}

JNIEXPORT jlong JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_read
  (JNIEnv* env, jclass self, jint pid, jlong addr)
{
	return ptrace(PTRACE_PEEKDATA, pid, addr, NULL);
}

JNIEXPORT void JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_write
  (JNIEnv* env, jclass self, jint pid, jlong addr, jlong value)
{
	POSIX_CHECK(ptrace(PTRACE_POKEDATA, pid, addr, value));
}

JNIEXPORT void JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_kill
  (JNIEnv* env, jclass self, jint pid, jint sig)
{
	POSIX_CHECK(kill(pid, sig));
}
