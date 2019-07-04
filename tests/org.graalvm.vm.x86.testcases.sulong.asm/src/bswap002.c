/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	unsigned long arg = 0x123456789ABCDEF0;
	unsigned long out = 0;
	__asm__("bswapq %%rax" : "=a"(out) : "a"(arg));
	return (out == 0xF0DEBC9A78563412);
}
