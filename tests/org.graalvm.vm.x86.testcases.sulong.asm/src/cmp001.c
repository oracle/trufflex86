/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>
#include "flags.h"

int main(void)
{
	char flags;
	__asm__("movb $0xFF, %%al\n\t"
		"cmpb $0x02, %%al\n\t"
		"lahf\n\t"
		"movb %%ah, %%al" : "=a"(flags));
	printf("%02X\n", flags & CC_MASK8);
}
