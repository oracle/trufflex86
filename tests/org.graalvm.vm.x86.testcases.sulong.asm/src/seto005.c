/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	char out = 0x55;
	__asm__("movb $0xFF, %%al; cmpb $0xFE, %%al; seto %%al" : "=a"(out));
	return out;
}
