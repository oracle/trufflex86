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

int wait_for_signal(pid_t pid)
{
	int status;
	int options = 0;
	siginfo_t info;

	waitpid(pid, &status, options);

	ptrace(PTRACE_GETSIGINFO, pid, NULL, &info);
	switch (info.si_signo) {
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
					printf("unknown SIGTRAP code: %d\n", info.si_code);
			}
			break;
		case SIGSEGV:
			printf("SIGSEGV: %d\n", info.si_code);
			break;
		default:
			printf("Got signal %s\n", strsignal(info.si_signo));
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
	} else {
		wait_for_signal(pid);
		free(cmd);
		return pid;
	}
}

JNIEXPORT jint JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_waitForSignal
  (JNIEnv* env, jclass self, jint pid)
{
	return wait_for_signal(pid);
}

JNIEXPORT jlong JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_syscall
  (JNIEnv* env, jclass self, jint pid, jlong nr, jlong a1, jlong a2, jlong a3, jlong a4, jlong a5, jlong a6)
{
	struct user_regs_struct regs;
	struct user_regs_struct saved_regs;
	uint64_t insn;
	uint64_t rip;

	// patch current insn to syscall
	ptrace(PTRACE_GETREGS, pid, NULL, &regs);
	saved_regs = regs;
	rip = regs.rip;
	insn = ptrace(PTRACE_PEEKDATA, pid, rip, NULL);
	ptrace(PTRACE_POKEDATA, pid, rip, 0xCCCCCCCCCCCC050FL);

	// set regs
	regs.rax = nr;
	regs.rdi = a1;
	regs.rsi = a2;
	regs.rdx = a3;
	regs.r10 = a4;
	regs.r8 = a5;
	regs.r9 = a6;
	ptrace(PTRACE_SETREGS, pid, NULL, &regs);

	// execute syscall
	ptrace(PTRACE_SINGLESTEP, pid, NULL, NULL);
	wait_for_signal(pid);

	// get regs
	ptrace(PTRACE_GETREGS, pid, NULL, &regs);

	// restore regs
	ptrace(PTRACE_SETREGS, pid, NULL, &saved_regs);

	// restore insn
	ptrace(PTRACE_POKEDATA, pid, rip, insn);

	return regs.rax;
}

JNIEXPORT jlong JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_read
  (JNIEnv* env, jclass self, jint pid, jlong addr)
{
	return ptrace(PTRACE_PEEKDATA, pid, addr, NULL);
}

JNIEXPORT void JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_write
  (JNIEnv* env, jclass self, jint pid, jlong addr, jlong value)
{
	ptrace(PTRACE_POKEDATA, pid, addr, value);
}

JNIEXPORT void JNICALL Java_org_graalvm_vm_x86_emu_Ptrace_kill
  (JNIEnv* env, jclass self, jint pid, jint sig)
{
	kill(pid, sig);
}
