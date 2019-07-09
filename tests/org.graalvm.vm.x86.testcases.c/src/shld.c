/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>

#include <stdio.h>
#include "flags.h"

void test_shldw(unsigned short value, unsigned short bits, int count)
{
	int out_flags;
	unsigned short out;
	__asm__ volatile("pushf\n\t"
			 "xorq %%rax, %%rax\n\t"
			 "push %%rax\n\t"
			 "popf\n\t"
			 "shldw %%cl, %[bits], %[value]\n\t"
			 "pushf\n\t"
			 "pop %%rax\n\t"
			 "popf\n\t"
			 :         "=a"(out_flags),
			   [value] "=r"(out)
			 : [bits]  "r"(bits),
			           "1"(value),
			           "c"(count));
	printf("%04hx:%04hx:%02x:%04hx:%04x\n", value, bits, count, out, (out_flags & CC_MASK));
}

int main(void)
{
	test_shldw(0x0000, 0x0000, 0);
	test_shldw(0x0000, 0x0000, 5);
	test_shldw(0x0000, 0x1000, 0);
	test_shldw(0x0000, 0x1000, 5);
	test_shldw(0x0000, 0xFFFF, 5);
	test_shldw(0x0000, 0x8000, 8);
	test_shldw(0x0000, 0x8001, 15);
	test_shldw(0x0000, 0x8001, 16);
	test_shldw(0x0000, 0xFFFF, 15);
	test_shldw(0x0000, 0xFFFF, 16);
	test_shldw(0x0001, 0x8000, 1);
	test_shldw(0x0001, 0xC000, 1);
	test_shldw(0x0001, 0xC000, 2);
	test_shldw(0x1001, 0xC000, 2);
	test_shldw(0x1001, 0xC000, 4);
	test_shldw(0x1001, 0xC000, 0);
	test_shldw(0xBEEF, 0xC0DE, 0);
	test_shldw(0xBABE, 0xEFDE, 8);
	test_shldw(0xBABE, 0xEFDE, 4);
	test_shldw(0x4000, 0x0000, 1);
	test_shldw(0x8000, 0x1000, 1);
	test_shldw(0x4000, 0x0000, 2);
	test_shldw(0x8000, 0x1000, 2);
	test_shldw(0xBEEF, 0xC0DE, 16);
	test_shldw(0xBEEF, 0xC0DE, 24);
	test_shldw(0xBEEF, 0xC0DE, 32);
	test_shldw(0xBEEF, 0xC0DE, 48);
	test_shldw(0xBEEF, 0xC0DE, 56);
	test_shldw(0xBEEF, 0xC0DE, 64);
	return 0;
}
