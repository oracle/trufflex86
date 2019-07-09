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

#include <stdlib.h>
#include <stdint.h>
#include <errno.h>
#include <signal.h>
#include <ucontext.h>
#include <sys/mman.h>

#include "org_graalvm_vm_memory_hardware_MMU.h"

#ifdef __x86_64__
#include "xed/xed-interface.h"
#endif

#include "libmemory.h"

static const char* posix_exception_name = "org/graalvm/vm/posix/api/PosixException";

#if defined(__x86_64__) || defined(__aarch64__)
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	return JNI_VERSION_1_2;
}
#else
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	/* not supported on this platform */
	return -1;
}
#endif

static void throw_posix_exception(JNIEnv* env, int err)
{
	jclass cls = (*env)->FindClass(env, posix_exception_name);
	jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
	jobject exc = (*env)->NewObject(env, cls, ctor, err);
	(*env)->Throw(env, exc);
}

JNIEXPORT jlong JNICALL Java_org_graalvm_vm_memory_hardware_MMU_setup
  (JNIEnv* env, jclass self, jlong lo, jlong hi)
{
	int err = 0;
	long result = MEM_setup_segv_handler(lo, hi, &err);
	if(result == -1) {
		if(err == 0) {
			return 0;
		} else {
			throw_posix_exception(env, errno);
			return 0;
		}
	} else {
		return result;
	}
}

JNIEXPORT jlong JNICALL Java_org_graalvm_vm_memory_hardware_MMU_mmap
  (JNIEnv* env, jclass self, jlong addr, jlong len, jboolean r, jboolean w, jboolean x, jboolean fixed, jboolean anonymous, jboolean shared, jint fildes, jlong off)
{
	int prot = 0;
	if(r) {
		prot |= PROT_READ;
	}
	if(w) {
		prot |= PROT_WRITE;
	}
	if(x) {
		prot |= PROT_EXEC;
	}
	int flags = 0;
	if(fixed) {
		flags |= MAP_FIXED;
	}
	if(anonymous) {
		flags |= MAP_ANONYMOUS;
	}
	if(shared) {
		flags |= MAP_SHARED;
	} else {
		flags |= MAP_PRIVATE;
	}
#ifdef DEBUG
	printf("mmap(%p, 0x%lx, ...)\n", addr, len);
	fflush(stdout);
#endif
	void* result = mmap((void*) addr, len, prot, flags, fildes, off);
	if(result == MAP_FAILED) {
		throw_posix_exception(env, errno);
		return 0;
	}
	return (long) result;
}

JNIEXPORT jint JNICALL Java_org_graalvm_vm_memory_hardware_MMU_munmap
  (JNIEnv* env, jclass self, jlong addr, jlong len)
{
#ifdef DEBUG
	printf("munmap(%p, 0x%lx, ...)\n", addr, len);
	fflush(stdout);
#endif
	int result = munmap((void*) addr, len);
	if(result < 0) {
		throw_posix_exception(env, errno);
		return 0;
	}
	return result;
}

JNIEXPORT jint JNICALL Java_org_graalvm_vm_memory_hardware_MMU_mprotect
  (JNIEnv* env, jclass self, jlong addr, jlong len, jboolean r, jboolean w, jboolean x)
{
	int prot = 0;
	if(r) {
		prot |= PROT_READ;
	}
	if(w) {
		prot |= PROT_WRITE;
	}
	if(x) {
		prot |= PROT_EXEC;
	}
	int result = mprotect((void*) addr, len, prot);
	if(result < 0) {
		throw_posix_exception(env, errno);
		return 0;
	}
	return 0;
}
