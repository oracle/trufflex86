/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	unsigned long value = 0;
	__asm__("movq $0x123456789ABCDEF, %%rax; pushq %%rax; popq %0" : "=r"(value) : : "%rax");
	return value == 0x123456789ABCDEF;
}
