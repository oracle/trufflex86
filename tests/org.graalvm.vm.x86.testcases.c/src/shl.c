/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>

#include <stdio.h>
#include "flags.h"

void test_shlw(unsigned short value, int count)
{
	int out_flags;
	unsigned short out;
	__asm__ volatile("pushf\n\t"
			 "xorq	%%rax, %%rax\n\t"
			 "push	%%rax\n\t"
			 "popf\n\t"
			 "shlw	%%cl, %[value]\n\t"
			 "pushf\n\t"
			 "pop	%%rax\n\t"
			 "popf\n\t"
			 :         "=a"(out_flags),
			   [value] "=r"(out)
			 :         "1"(value),
			           "c"(count));
	printf("%04hx:%02x:%04hx:%04x\n", value, count, out, (out_flags & CC_MASK));
}

void test_shll_1(unsigned int value)
{
	int out_flags;
	unsigned int out;
	__asm__ volatile("pushf\n\t"
			 "xorq	%%rax, %%rax\n\t"
			 "push	%%rax\n\t"
			 "popf\n\t"
			 "shll	$1, %[value]\n\t"
			 "pushf\n\t"
			 "pop	%%rax\n\t"
			 "popf\n\t"
			 :         "=a"(out_flags),
			   [value] "=r"(out)
			 :         "1"(value));
	printf("%08x:1:%08x:%04x\n", value, out, (out_flags & CC_MASK));
}

void test_shlq_32(unsigned long value)
{
	int out_flags;
	unsigned long out;
	__asm__ volatile("pushf\n\t"
			 "xorq	%%rax, %%rax\n\t"
			 "push	%%rax\n\t"
			 "popf\n\t"
			 "shlq	$0x20, %[value]\n\t"
			 "pushf\n\t"
			 "pop	%%rax\n\t"
			 "popf\n\t"
			 :         "=a"(out_flags),
			   [value] "=r"(out)
			 :         "1"(value));
	printf("%016lx:20:%016lx:%04x\n", value, out, (out_flags & CC_MASK));
}

int main(void)
{
	test_shlw(0x0000, 0);
	test_shlw(0x0000, 5);
	test_shlw(0x1000, 0);
	test_shlw(0x1000, 5);
	test_shlw(0xFFFF, 5);
	test_shlw(0x8000, 8);
	test_shlw(0x8001, 15);
	test_shlw(0x8001, 16);
	test_shlw(0xFFFF, 15);
	test_shlw(0xFFFF, 16);
	test_shlw(0x8000, 1);
	test_shlw(0xC000, 1);
	test_shlw(0xC000, 2);
	test_shlw(0xC000, 2);
	test_shlw(0xC000, 4);
	test_shlw(0xC000, 0);
	test_shlw(0xC0DE, 0);
	test_shlw(0xEFDE, 8);
	test_shlw(0xEFDE, 4);
	test_shlw(0x0000, 1);
	test_shlw(0x1000, 1);
	test_shlw(0x0000, 2);
	test_shlw(0x1000, 2);
	test_shlw(0xC0DE, 16);
	test_shlw(0xC0DE, 24);
	test_shlw(0xC0DE, 32);
	test_shlw(0xC0DE, 48);
	test_shlw(0xC0DE, 56);
	test_shlw(0xC0DE, 64);

	test_shll_1(0xdffffdea);
	test_shll_1(0x0ffffdea);
	test_shll_1(0x0ffffde0);

	test_shlq_32(0x0000000000000163L);
	return 0;
}
