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

int main(void)
{
	VENDOR vendor;
	BRAND brand;
	unsigned int a, b, c, d;

	__cpuid(0, a, vendor.i.b, vendor.i.c, vendor.i.d);
	vendor.str[12] = 0;
	printf("Vendor: '%s'\n", vendor.str);

	__cpuid(0x80000000, a, b, c, d);
	if(a < 0x80000004) {
		printf("brand string not supported on this CPU\n");
		return 0;
	}
	__cpuid(0x80000002, brand.i.a1, brand.i.b1, brand.i.c1, brand.i.d1);
	__cpuid(0x80000003, brand.i.a2, brand.i.b2, brand.i.c2, brand.i.d2);
	__cpuid(0x80000004, brand.i.a3, brand.i.b3, brand.i.c3, brand.i.d3);
	brand.str[48] = 0;
	printf("Brand:  '%s'\n", brand.str);
	return 0;
}
