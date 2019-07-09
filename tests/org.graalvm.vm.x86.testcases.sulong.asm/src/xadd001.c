/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	unsigned char arg1 = 0x12;
	unsigned char arg2 = 0x34;
	unsigned char out1 = 0;
	unsigned char out2 = 0;
	__asm__("xaddb %%al, %%cl" : "=a"(out1), "=c"(out2) : "a"(arg1), "c"(arg2));
	return (out1 == 0x34) && (out2 == 0x46);
}
