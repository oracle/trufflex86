/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	unsigned short arg = 0x1234;
	unsigned short out = 0;
	__asm__("bsfw %%ax, %%cx" : "=c"(out) : "a"(arg));
	return (out == 2);
}
