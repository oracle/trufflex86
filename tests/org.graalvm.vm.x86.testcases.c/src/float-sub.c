#include <math.h>
#include <stdio.h>
#include <stdint.h>

typedef union {
	float	f;
	int32_t	i;
} FLOAT;

void test(FLOAT a, FLOAT b)
{
	FLOAT diff = { .f = a.f - b.f };
	printf("%08x:%08x:%08x\n", a.i, b.i, diff.i);
}

#define TEST(x, y)	test((FLOAT) { .f = (x) }, (FLOAT) { .f = (y) })

int main(void)
{
	TEST(0, 0);
	TEST(1, 0);
	TEST(1, 1);
	TEST(21, 42);
	TEST(-21, 42);
	TEST(21, -42);
	TEST(1E-8, 1);
	TEST(1E-18, 1);
	TEST(1, 1E-8);
	TEST(1, 1E-18);
	TEST(1E8, 1E-18);
	TEST(1E-8, 1E18);
	TEST(NAN, 0);
	TEST(NAN, 1);
	TEST(NAN, -1);
	TEST(NAN, NAN);
	TEST(INFINITY, 0);
	TEST(INFINITY, 1);
	TEST(INFINITY, -1);
	TEST(INFINITY, INFINITY);
	TEST(INFINITY, 1E27F);
	TEST(INFINITY, -1E27F);
	TEST(1E27F, -1E27F);
	TEST(1E27F, 1E27F);
	return 0;
}
