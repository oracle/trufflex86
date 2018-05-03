/*
 * Test 64x64 -> 128 multiply subroutines
 *
 * This work is licensed under the terms of the GNU LGPL, version 2 or later.
 * See the COPYING.LIB file in the top-level directory.
 *
 */

#include <stdio.h>
#include <stdint.h>
#include <assert.h>

#define ARRAY_SIZE(x) (sizeof(x) / sizeof(*(x)))

typedef struct {
	uint64_t a, b;
	uint64_t rh, rl;
} Test;

static const Test test_u_data[] = {
	{ 1, 1, 0, 1 },
	{ 10000, 10000, 0, 100000000 },
	{ 0xffffffffffffffffULL, 2, 1, 0xfffffffffffffffeULL },
	{ 0xffffffffffffffffULL, 0xffffffffffffffffULL, 0xfffffffffffffffeULL, 0x0000000000000001ULL },
	{ 0x1122334455667788ull, 0x8877665544332211ull, 0x092228fb777ae38full, 0x0a3e963337c60008ull },
};

static const Test test_s_data[] = {
	{ 1, 1, 0, 1 },
	{ 1, -1, -1, -1 },
	{ -10, -10, 0, 100 },
	{ 10000, 10000, 0, 100000000 },
	{ -1, 2, -1, -2 },
	{ 0x1122334455667788ULL, 0x1122334455667788ULL, 0x01258f60bbc2975cULL, 0x1eace4a3c82fb840ULL },
};

static void mulu64(uint64_t* rl, uint64_t* rh, uint64_t a, uint64_t b)
{
	__asm__("mulq %[b]" : "=a"(*rl), "=d"(*rh) : "a"(a), [b] "r"(b));
}

static void muls64(int64_t* rl, int64_t* rh, int64_t a, int64_t b)
{
	__asm__("imulq %[b]" : "=a"(*rl), "=d"(*rh) : "a"(a), [b] "r"(b));
}

static void test_u(void)
{
	int i;

	for(i = 0; i < ARRAY_SIZE(test_u_data); ++i) {
		uint64_t rl, rh;
		mulu64(&rl, &rh, test_u_data[i].a, test_u_data[i].b);
		assert(rl == test_u_data[i].rl);
		assert(rh == test_u_data[i].rh);
	}
}

static void test_s(void)
{
	int i;

	for(i = 0; i < ARRAY_SIZE(test_s_data); ++i) {
		uint64_t rl, rh;
		muls64((int64_t*) &rl, (int64_t*) &rh, test_s_data[i].a, test_s_data[i].b);
		assert(rl == test_s_data[i].rl);
		assert(rh == test_s_data[i].rh);
	}
}

int main(int argc, char **argv)
{
	test_s();
	test_u();
	return 0;
}
