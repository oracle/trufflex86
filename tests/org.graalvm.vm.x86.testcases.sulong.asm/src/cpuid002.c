/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <cpuid.h>
#include <string.h>
#include <stdio.h>

typedef union {
	struct {
		unsigned int b, d, c;
	} i;
	char str[13];
} VENDOR;

int main(void)
{
	VENDOR vendor;
	unsigned int a;

	__cpuid(0, a, vendor.i.b, vendor.i.c, vendor.i.d);
	vendor.str[12] = 0;
	printf("'%s'\n", vendor.str);

	return 0;
}
