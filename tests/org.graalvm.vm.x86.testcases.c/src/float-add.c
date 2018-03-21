#include <math.h>
#include <stdio.h>
#include <stdint.h>

void test(float a, float b)
{
	float sum = a + b;
	int32_t* ia = (int32_t*) &a;
	int32_t* ib = (int32_t*) &b;
	int32_t* is = (int32_t*) &sum;
	printf("%08x:%08x:%08x\n", *ia, *ib, *is);
}

int main(void)
{
	test(0, 0);
	test(1, 0);
	test(1, 1);
	test(21, 42);
	test(-21, 42);
	test(21, -42);
	test(1E-8, 1);
	test(1E-18, 1);
	test(1, 1E-8);
	test(1, 1E-18);
	test(1E8, 1E-18);
	test(1E-8, 1E18);
	test(NAN, 0);
	test(NAN, 1);
	test(NAN, -1);
	test(NAN, NAN);
	test(INFINITY, 0);
	test(INFINITY, 1);
	test(INFINITY, -1);
	test(INFINITY, INFINITY);
	test(INFINITY, 1E27F);
	test(INFINITY, -1E27F);
	test(1E27F, -1E27F);
	test(1E27F, 1E27F);
	return 0;
}
