/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	unsigned int arg = 0x1234;
	unsigned int out = 0;
	__asm__("xchgb %%al, %%ah" : "=a"(out) : "a"(arg));
	return (out == 0x3412);
}
