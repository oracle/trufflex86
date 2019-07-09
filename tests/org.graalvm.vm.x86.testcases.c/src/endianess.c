/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>

int main(void)
{
	volatile unsigned long u64;
	volatile unsigned char* u8p = (unsigned char*) &u64;
	int i;

	u64 = 0x123456789ABCDEF0L;
	printf("Bytes:");
	for(i = 0; i < 8; i++) {
		printf(" %02X", u8p[i]);
	}
	printf("\n");
	return 0;
}
