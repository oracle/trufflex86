#include <stdio.h>
#define FMTLX "%016lx"

#define TEST_BSX(op, size, op0) {					       \
	long res, val, resz;						       \
	val = op0;							       \
	__asm__("xor %1, %1\n"						       \
		"mov $0x12345678, %0\n" #op " %" size "2, %" size "0\n"	       \
		"setz %b1"						       \
		: "=&r"(res), "=&q"(resz)				       \
		: "r"(val));						       \
	printf("%-10s A=" FMTLX " R=" FMTLX " %ld\n", #op, val, res, resz);    \
}

int main(void)
{
	TEST_BSX(bsrw, "w", 0);
	TEST_BSX(bsrw, "w", 0x12340128);
	TEST_BSX(bsfw, "w", 0);
	TEST_BSX(bsfw, "w", 0x12340128);
	TEST_BSX(bsrl, "k", 0);
	TEST_BSX(bsrl, "k", 0x00340128);
	TEST_BSX(bsfl, "k", 0);
	TEST_BSX(bsfl, "k", 0x00340128);
	TEST_BSX(bsrq, "", 0);
	TEST_BSX(bsrq, "", 0x003401281234);
	TEST_BSX(bsfq, "", 0);
	TEST_BSX(bsfq, "", 0x003401281234);
	return 0;
}
