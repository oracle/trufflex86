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
