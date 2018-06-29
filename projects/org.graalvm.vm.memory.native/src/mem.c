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

static const char* posix_exception_name = "com/everyware/posix/api/PosixException";

static void (*jvm_sigsegv_handler)(int, siginfo_t *, void *);

static unsigned long low;
static unsigned long high;

static unsigned long error;

static const char digits[36] = "0123456789ABCDEF";

#ifdef __x86_64__
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	xed_tables_init();
	return JNI_VERSION_1_2;
}
#else
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	/* not supported on this platform */
	return -1;
}
#endif

#ifdef DEBUG
#define __SYSCALL_3(result, id, a1, a2, a3)                                    \
	__asm__ volatile("syscall" : "=a"(result)                              \
				   : "a"(id), "D"(a1), "S"(a2), "d"(a3)        \
				   : "memory", "rcx", "r11");

static void write0(int fd, const void* data, long len)
{
	long result;
	__SYSCALL_3(result, 1, (long) fd, (long) data, (long) len);
}

static void print_str(const char* s)
{
	int len = 0;
	char* p;
	for(p = (char*) s; *p; p++);
	len = p - s;
	write0(1, s, len);
}

static void print_i8(const char value)
{
	char buf[2];
	buf[0] = digits[(value >> 4) & 0x0F];
	buf[1] = digits[value & 0x0F];
	write0(1, buf, 2);
}

static void print_i64(const long value)
{
	int i;
	char buf[16];
	for(i = 0; i < 16; i++) {
		buf[i] = digits[(value >> 60 - (4 * i)) & 0x0F];
	}
	write0(1, buf, 16);
}
#endif

#ifdef __x86_64__
static int get_insn_length(unsigned char* pc)
{
	xed_decoded_inst_t xedd;
	xed_state_t dstate;

	dstate.mmode = XED_MACHINE_MODE_LONG_64;

	xed_decoded_inst_zero_set_mode(&xedd, &dstate);
	xed_ild_decode(&xedd, pc, XED_MAX_INSTRUCTION_BYTES);
	return xed_decoded_inst_get_length(&xedd);
}

static void sigsegv_handler(int sig, siginfo_t* info, void* context)
{
	ucontext_t* ctx = (ucontext_t*) context;
	unsigned long ptr = (unsigned long) info->si_addr;
	if(ptr >= low && ptr <= high) {
		int i;
		error = ptr;
		unsigned char* pc = (unsigned char*) ctx->uc_mcontext.gregs[REG_RIP];
		int insn_len = get_insn_length(pc);
#ifdef DEBUG
		print_str("segfault at 0x");
		print_i64(ptr);
		print_str("; within range; insn length: 0x");
		print_i8(insn_len);
		print_str("\ninsn:");
		for(i = 0; i < insn_len; i++) {
			print_str(" ");
			print_i8(pc[i]);
		}
		print_str("\n");
#endif

		uintptr_t rip = ctx->uc_mcontext.gregs[REG_RIP];
		rip += insn_len;
		ctx->uc_mcontext.gregs[REG_RIP] = rip;
	} else {
		jvm_sigsegv_handler(sig, info, context);
	}
}
#else
static void sigsegv_handler(int sig, siginfo_t* info, void* context)
{
	printf("SIGSEGV handler not supported on this platform\n");
	exit(1);
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
	struct sigaction sa;

	low = lo;
	high = hi;

	if(sigaction(SIGSEGV, NULL, &sa) == -1) {
		throw_posix_exception(env, errno);
	}

	if(!(sa.sa_flags & SA_SIGINFO)) {
		return 0;
	}

	jvm_sigsegv_handler = sa.sa_sigaction;
	sa.sa_sigaction = sigsegv_handler;

	if(sigaction(SIGSEGV, &sa, NULL) == -1) {
		throw_posix_exception(env, errno);
	}

	error = 0;
	return (jlong) &error;
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
