#ifndef __QEMU_COMMON_H__
#define __QEMU_COMMON_H__

#define _GNU_SOURCE
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <inttypes.h>
#include <math.h>
#include <signal.h>
#include <setjmp.h>
#include <errno.h>
#include <sys/ucontext.h>
#include <sys/mman.h>

#define FMT64X "%016lx"
#define FMTLX "%016lx"

#define xglue(x, y) x##y
#define glue(x, y) xglue(x, y)
#define stringify(s) tostring(s)
#define tostring(s) #s

#define CC_C 0x0001
#define CC_P 0x0004
#define CC_A 0x0010
#define CC_Z 0x0040
#define CC_S 0x0080
#define CC_O 0x0800

#define __init_call __attribute__((unused, __section__("initcall")))

#define asm __asm__

#define CC_MASK (CC_C | CC_P | CC_Z | CC_S | CC_O | CC_A)

static inline long i2l(long v)
{
	return v | ((v ^ 0xabcd) << 32);
}

#endif
