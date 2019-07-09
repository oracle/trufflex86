/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>
#include <string.h>

int main(void)
{
	unsigned char buf[16];
	unsigned long out;
	unsigned int i;

	memset(buf, 0xCC, sizeof(buf));

	__asm__("cld\n"
		"lea %1, %%rdi\n"
		"movb $0x42, %%al\n"
		"movq $10, %%rcx\n"
		"rep stosb\n"
		"movq %%rdi, %0"
		: "=r"(out)
		: "m"(buf[2])
		: "rax", "rcx", "rdi");
	printf("buf:");
	for (i = 0; i < 16; i++)
	printf(" %02X", buf[i]);
	printf("\n");
	return (out == ((unsigned long)&buf[12]));
}
