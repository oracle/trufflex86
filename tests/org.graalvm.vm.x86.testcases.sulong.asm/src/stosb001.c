/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	volatile unsigned char buf = 0xCC;
	unsigned long out;
	__asm__("cld\n"
		"lea %1, %%rdi\n"
		"movb $0x42, %%al\n"
		"stosb\n"
		"movq %%rdi, %0"
		: "=r"(out)
		: "m"(buf)
		: "rax", "rdi");
	return (out == ((unsigned long)&buf + 1)) && (buf == 0x42);
}
