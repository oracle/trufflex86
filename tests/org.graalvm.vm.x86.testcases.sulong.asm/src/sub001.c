#include <stdio.h>
#include "flags.h"

void test_sub(int a, int b)
{
	int out_flags;
	int out;
	__asm__ volatile("pushf\n"
	                 "xorq %%rax, %%rax\n"
	                 "push %%rax\n"
	                 "popf\n"
	                 "subl %[a], %[b]\n"
	                 "pushf\n"
	                 "pop %%rax\n"
	                 "popf\n"
	                 : "=a"(out_flags), [b] "=r"(out)
	                 : [a] "r"(a), "1"(b));
	printf("%08x:%08x:%08x:%x:%x:%x\n", a, b, out, (out_flags & CC_C) ? 1 : 0, (out_flags & CC_O) ? 1 : 0, (out_flags & CC_Z) ? 1 : 0);
}

int main(void)
{
	test_sub(0x00000000, 0x00000000);
	test_sub(0x00000000, 0x00000d0c);
	test_sub(0x00000d0c, 0x00000000);
	test_sub(0x00000d0c, 0x00000d0c);
	test_sub(0xffffffff, 0x00000000);
	test_sub(0xffffffff, 0x00000001);
	test_sub(0xffffffff, 0x00000d0c);
	test_sub(0xffffffff, 0x80000000);
	test_sub(0xffffffff, 0xffffffff);
	test_sub(0x80000000, 0x00000000);
	test_sub(0x80000000, 0x00000d0c);
	test_sub(0x80000000, 0x80000000);
	test_sub(0x80000000, 0xffffffff);
}
