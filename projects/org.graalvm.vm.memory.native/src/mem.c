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

static const char* posix_exception_name = "com/everyware/posix/api/PosixException";

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
