#include "qemu-common.h"

#undef CC_MASK
#define CC_MASK (CC_C | CC_P | CC_Z | CC_S | CC_O | CC_A)

#define OP mul
#include "qemu-muldiv.h"

#define OP imul
#include "qemu-muldiv.h"

void test_imulw2(long op0, long op1)
{
	long res, s1, s0, flags;
	s0 = op0;
	s1 = op1;
	res = s0;
	flags = 0;
	asm volatile("push %4\n\t"
		     "popf\n\t"
		     "imulw %w2, %w0\n\t"
		     "pushf\n\t"
		     "pop %1\n\t"
		     : "=q"(res), "=g"(flags)
		     : "q"(s1), "0"(res), "1"(flags));
	printf("%-10s A=" FMTLX " B=" FMTLX " R=" FMTLX " CC=%04lx\n", "imulw",
			s0, s1, res, flags & CC_MASK);
}

void test_imull2(long op0, long op1)
{
	long res, s1, s0, flags;
	s0 = op0;
	s1 = op1;
	res = s0;
	flags = 0;
	asm volatile("push %4\n\t"
		     "popf\n\t"
		     "imull %k2, %k0\n\t"
		     "pushf\n\t"
		     "pop %1\n\t"
		     : "=q"(res), "=g"(flags)
		     : "q"(s1), "0"(res), "1"(flags));
	printf("%-10s A=" FMTLX " B=" FMTLX " R=" FMTLX " CC=%04lx\n", "imull",
			s0, s1, res, flags & CC_MASK);
}

void test_imulq2(long op0, long op1)
{
	long res, s1, s0, flags;
	s0 = op0;
	s1 = op1;
	res = s0;
	flags = 0;
	asm volatile("push %4\n\t"
		     "popf\n\t"
		     "imulq %2, %0\n\t"
		     "pushf\n\t"
		     "pop %1\n\t"
		     : "=q"(res), "=g"(flags)
		     : "q"(s1), "0"(res), "1"(flags));
	printf("%-10s A=" FMTLX " B=" FMTLX " R=" FMTLX " CC=%04lx\n", "imulq",
			s0, s1, res, flags & CC_MASK);
}

#define TEST_IMUL_IM(size, rsize, op0, op1) {				       \
	long res, flags, s1;						       \
	flags = 0;							       \
	res = 0;							       \
	s1 = op1;							       \
	asm volatile("push %3\n\t"					       \
		     "popf\n\t"						       \
		     "imul" size " $" #op0 ", %" rsize "2, %" rsize "0\n\t"    \
		     "pushf\n\t"					       \
		     "pop %1\n\t"					       \
		     : "=r"(res), "=g"(flags)				       \
		     : "r"(s1), "1"(flags), "0"(res));			       \
	printf("%-10s A=" FMTLX " B=" FMTLX " R=" FMTLX " CC=%04lx\n",	       \
			"imul" size " im", (long)op0, (long)op1, res,	       \
			flags &CC_MASK);				       \
}

#undef CC_MASK
#define CC_MASK (0)

#define OP div
#include "qemu-muldiv.h"

#define OP idiv
#include "qemu-muldiv.h"

int main(void) {
	test_imulb(0x1234561d, 4);
	test_imulb(3, -4);
	test_imulb(0x80, 0x80);
	test_imulb(0x10, 0x10);

	test_imulw(0, 0x1234001d, 45);
	test_imulw(0, 23, -45);
	test_imulw(0, 0x8000, 0x8000);
	test_imulw(0, 0x100, 0x100);

	test_imull(0, 0x1234001d, 45);
	test_imull(0, 23, -45);
	test_imull(0, 0x80000000, 0x80000000);
	test_imull(0, 0x10000, 0x10000);

	test_mulb(0x1234561d, 4);
	test_mulb(3, -4);
	test_mulb(0x80, 0x80);
	test_mulb(0x10, 0x10);

	test_mulw(0, 0x1234001d, 45);
	test_mulw(0, 23, -45);
	test_mulw(0, 0x8000, 0x8000);
	test_mulw(0, 0x100, 0x100);

	test_mull(0, 0x1234001d, 45);
	test_mull(0, 23, -45);
	test_mull(0, 0x80000000, 0x80000000);
	test_mull(0, 0x10000, 0x10000);

	test_imulw2(0x1234001d, 45);
	test_imulw2(23, -45);
	test_imulw2(0x8000, 0x8000);
	test_imulw2(0x100, 0x100);

	test_imull2(0x1234001d, 45);
	test_imull2(23, -45);
	test_imull2(0x80000000, 0x80000000);
	test_imull2(0x10000, 0x10000);

	TEST_IMUL_IM("w", "w", 45, 0x1234);
	TEST_IMUL_IM("w", "w", -45, 23);
	TEST_IMUL_IM("w", "w", 0x8000, 0x80000000);
	TEST_IMUL_IM("w", "w", 0x7fff, 0x1000);

	TEST_IMUL_IM("l", "k", 45, 0x1234);
#include <stdio.h>
#include "flags.h"
#define FMTLX "%016lx"

#undef CC_MASK
#define CC_MASK (CC_C | CC_P | CC_Z | CC_S | CC_O | CC_A)
	TEST_IMUL_IM("l", "k", -45, 23);
	TEST_IMUL_IM("l", "k", 0x8000, 0x80000000);
	TEST_IMUL_IM("l", "k", 0x7fff, 0x1000);

	test_idivb(0x12341678, 0x127e);
	test_idivb(0x43210123, -5);
	test_idivb(0x12340004, -1);

	test_idivw(0, 0x12345678, 12347);
	test_idivw(0, -23223, -45);
	test_idivw(0, 0x12348000, -1);
	test_idivw(0x12343, 0x12345678, 0x81238567);

	test_idivl(0, 0x12345678, 12347);
	test_idivl(0, -233223, -45);
	test_idivl(0, 0x80000000, -1);
	test_idivl(0x12343, 0x12345678, 0x81234567);

	test_divb(0x12341678, 0x127e);
	test_divb(0x43210123, -5);
	test_divb(0x12340004, -1);

	test_divw(0, 0x12345678, 12347);
	test_divw(0, -23223, -45);
	test_divw(0, 0x12348000, -1);
	test_divw(0x12343, 0x12345678, 0x81238567);

	test_divl(0, 0x12345678, 12347);
	test_divl(0, -233223, -45);
	test_divl(0, 0x80000000, -1);
	test_divl(0x12343, 0x12345678, 0x81234567);

	test_imulq(0, 0x1234001d1234001d, 45);
	test_imulq(0, 23, -45);
	test_imulq(0, 0x8000000000000000, 0x8000000000000000);
	test_imulq(0, 0x100000000, 0x100000000);

	test_mulq(0, 0x1234001d1234001d, 45);
	test_mulq(0, 23, -45);
	test_mulq(0, 0x8000000000000000, 0x8000000000000000);
	test_mulq(0, 0x100000000, 0x100000000);

	test_imulq2(0x1234001d1234001d, 45);
	test_imulq2(23, -45);
	test_imulq2(0x8000000000000000, 0x8000000000000000);
	test_imulq2(0x100000000, 0x100000000);

	TEST_IMUL_IM("q", "", 45, 0x12341234);
	TEST_IMUL_IM("q", "", -45, 23);
	TEST_IMUL_IM("q", "", 0x8000, 0x8000000000000000);
	TEST_IMUL_IM("q", "", 0x7fff, 0x10000000);

	test_idivq(0, 0x12345678abcdef, 12347);
	test_idivq(0, -233223, -45);
	test_idivq(0, 0x8000000000000000, -1);
	test_idivq(0x12343, 0x12345678, 0x81234567);

	test_divq(0, 0x12345678abcdef, 12347);
	test_divq(0, -233223, -45);
	test_divq(0, 0x8000000000000000, -1);
	test_divq(0x12343, 0x12345678, 0x81234567);
	return 0;
}
