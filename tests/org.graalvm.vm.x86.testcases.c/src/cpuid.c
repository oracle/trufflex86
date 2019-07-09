/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <cpuid.h>
#include <stdio.h>

typedef union {
	struct {
		unsigned int b, d, c;
	} i;
	char str[13];
} VENDOR;

typedef union {
	struct {
		unsigned int a1, b1, c1, d1;
		unsigned int a2, b2, c2, d2;
		unsigned int a3, b3, c3, d3;
	} i;
	char str[49];
} BRAND;

#if 0
 3:0 – Stepping
 7:4 – Model
11:8 – Family
13:12 – Processor Type
19:16 – Extended Model
27:20 – Extended Family
#endif
void print_info(int eax)
{
	int stepping	=  eax        & 0x0F;
	int model	= (eax >>  4) & 0x0F;
	int family	= (eax >>  8) & 0x0F;
	int type	= (eax >> 12) & 0x0F;
	int xmodel	= (eax >> 16) & 0x0F;
	int xfamily	= (eax >> 20) & 0xFF;

	if(family == 6) {
		model += xmodel << 4;
	} else if(family == 15) {
		family += xfamily;
		model += xmodel << 4;
	}

	printf("[info]:   0x%08x\n", eax);
	printf("Family:   %d\n", family);
	printf("Model:    %d\n", model);
	printf("Stepping: %d\n", stepping);
	printf("Type:     %d\n", type);
}

int main(void)
{
	VENDOR vendor;
	BRAND brand;
	unsigned int a, b, c, d;

	__cpuid(0, a, vendor.i.b, vendor.i.c, vendor.i.d);
	vendor.str[12] = 0;
	printf("Vendor:   '%s'\n", vendor.str);

	__cpuid(0x80000000, a, b, c, d);
	if(a < 0x80000004) {
		printf("brand string not supported on this CPU\n");
		return 0;
	}
	__cpuid(0x80000002, brand.i.a1, brand.i.b1, brand.i.c1, brand.i.d1);
	__cpuid(0x80000003, brand.i.a2, brand.i.b2, brand.i.c2, brand.i.d2);
	__cpuid(0x80000004, brand.i.a3, brand.i.b3, brand.i.c3, brand.i.d3);
	brand.str[48] = 0;
	printf("Brand:    '%s'\n", brand.str);

	__cpuid(1, a, b, c, d);
	print_info(a);

	return 0;
}
