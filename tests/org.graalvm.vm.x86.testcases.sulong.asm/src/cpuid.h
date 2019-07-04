/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <cpuid.h>

#define RDRND (1 << 30)
#define RDSEED (1 << 18)

static inline int has_rdrand(void)
{
	unsigned int a;
	unsigned int b;
	unsigned int c;
	unsigned int d;
	if(!__get_cpuid(0x1, &a, &b, &c, &d))
		return 0;
	return c & RDRND;
}

static inline int has_rdseed(void)
{
	unsigned int a;
	unsigned int b;
	unsigned int c;
	unsigned int d;
	if(!__get_cpuid(0x7, &a, &b, &c, &d))
		return 0;
	return b & RDSEED;
}
