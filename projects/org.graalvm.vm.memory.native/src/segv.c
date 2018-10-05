#define _GNU_SOURCE

#include <stdlib.h>
#include <stdint.h>
#include <errno.h>
#include <signal.h>
#include <ucontext.h>
#include <sys/mman.h>

#ifdef __x86_64__
#include "xed/xed-interface.h"
#endif

#include "libmemory.h"

static void (*jvm_sigsegv_handler)(int, siginfo_t *, void *);

static unsigned int init_once = 0;
static unsigned long low;
static unsigned long high;

static unsigned long error;

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
		uintptr_t rip = ctx->uc_mcontext.gregs[REG_RIP];
		rip += insn_len;
		ctx->uc_mcontext.gregs[REG_RIP] = rip;
	} else {
		jvm_sigsegv_handler(sig, info, context);
	}
}
#elif defined(__aarch64__)
static void sigsegv_handler(int sig, siginfo_t* info, void* context)
{
	ucontext_t* ctx = (ucontext_t*) context;
	unsigned long ptr = (unsigned long) info->si_addr;
	if(ptr >= low && ptr <= high) {
		int i;
		error = ptr;
		uintptr_t pc = ctx->uc_mcontext.pc;
		pc += 4;
		ctx->uc_mcontext.pc = pc;
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

long MEM_setup_segv_handler(long lo, long hi, int* err)
{
#ifdef __x86_64__
	if(!init_once) {
		xed_tables_init();
		init_once = 1;
	}
#endif

	struct sigaction sa;

	low = lo;
	high = hi;

	if(sigaction(SIGSEGV, NULL, &sa) == -1) {
		*err = errno;
		return -1;
	}

	if(!(sa.sa_flags & SA_SIGINFO)) {
		*err = 0;
		return -1;
	}

	jvm_sigsegv_handler = sa.sa_sigaction;
	sa.sa_sigaction = sigsegv_handler;

	if(sigaction(SIGSEGV, &sa, NULL) == -1) {
		*err = errno;
		return -1;
	}

	*err = 0;
	error = 0;
	return (long) &error;
}
