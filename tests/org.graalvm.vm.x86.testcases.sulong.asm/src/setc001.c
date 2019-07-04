/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	char out = 0x55;
	__asm__("movl $0x42, %%eax; cmpl $0x24, %%eax; setc %%al" : "=a"(out));
	return out;
}
