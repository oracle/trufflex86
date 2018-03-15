#include "qemu-common.h"

/* lea test (modrm support) */
#define TEST_LEAQ(STR) {						       \
	__asm__("lea " STR ", %0"					       \
		: "=r"(res)						       \
		: "a"(eax), "b"(ebx), "c"(ecx), "d"(edx), "S"(esi), "D"(edi)); \
	printf("lea %s = " FMTLX "\n", STR, res);			       \
}

#define TEST_LEA(STR) {							       \
	__asm__("lea " STR ", %0"					       \
		: "=r"(res)						       \
		: "a"(eax), "b"(ebx), "c"(ecx), "d"(edx), "S"(esi), "D"(edi)); \
	printf("lea %s = " FMTLX "\n", STR, res);			       \
}

int main(void)
{
	long eax, ebx, ecx, edx, esi, edi, res;
	eax = i2l(0x0001);
	ebx = i2l(0x0002);
	ecx = i2l(0x0004);
	edx = i2l(0x0008);
	esi = i2l(0x0010);
	edi = i2l(0x0020);

	TEST_LEA("0x4000");

	TEST_LEA("(%%eax)");
	TEST_LEA("(%%ebx)");
	TEST_LEA("(%%ecx)");
	TEST_LEA("(%%edx)");
	TEST_LEA("(%%esi)");
	TEST_LEA("(%%edi)");

	TEST_LEA("0x40(%%eax)");
	TEST_LEA("0x40(%%ebx)");
	TEST_LEA("0x40(%%ecx)");
	TEST_LEA("0x40(%%edx)");
	TEST_LEA("0x40(%%esi)");
	TEST_LEA("0x40(%%edi)");

	TEST_LEA("0x4000(%%eax)");
	TEST_LEA("0x4000(%%ebx)");
	TEST_LEA("0x4000(%%ecx)");
	TEST_LEA("0x4000(%%edx)");
	TEST_LEA("0x4000(%%esi)");
	TEST_LEA("0x4000(%%edi)");

	TEST_LEA("(%%eax, %%ecx)");
	TEST_LEA("(%%ebx, %%edx)");
	TEST_LEA("(%%ecx, %%ecx)");
	TEST_LEA("(%%edx, %%ecx)");
	TEST_LEA("(%%esi, %%ecx)");
	TEST_LEA("(%%edi, %%ecx)");

	TEST_LEA("0x40(%%eax, %%ecx)");
	TEST_LEA("0x4000(%%ebx, %%edx)");

	TEST_LEA("(%%ecx, %%ecx, 2)");
	TEST_LEA("(%%edx, %%ecx, 4)");
	TEST_LEA("(%%esi, %%ecx, 8)");

	TEST_LEA("(,%%eax, 2)");
	TEST_LEA("(,%%ebx, 4)");
	TEST_LEA("(,%%ecx, 8)");

	TEST_LEA("0x40(,%%eax, 2)");
	TEST_LEA("0x40(,%%ebx, 4)");
	TEST_LEA("0x40(,%%ecx, 8)");

	TEST_LEA("-10(%%ecx, %%ecx, 2)");
	TEST_LEA("-10(%%edx, %%ecx, 4)");
	TEST_LEA("-10(%%esi, %%ecx, 8)");

	TEST_LEA("0x4000(%%ecx, %%ecx, 2)");
	TEST_LEA("0x4000(%%edx, %%ecx, 4)");
	TEST_LEA("0x4000(%%esi, %%ecx, 8)");

	TEST_LEAQ("0x4000");
	TEST_LEAQ("0x4000(%%rip)");

	TEST_LEAQ("(%%rax)");
	TEST_LEAQ("(%%rbx)");
	TEST_LEAQ("(%%rcx)");
	TEST_LEAQ("(%%rdx)");
	TEST_LEAQ("(%%rsi)");
	TEST_LEAQ("(%%rdi)");

	TEST_LEAQ("0x40(%%rax)");
	TEST_LEAQ("0x40(%%rbx)");
	TEST_LEAQ("0x40(%%rcx)");
	TEST_LEAQ("0x40(%%rdx)");
	TEST_LEAQ("0x40(%%rsi)");
	TEST_LEAQ("0x40(%%rdi)");

	TEST_LEAQ("0x4000(%%rax)");
	TEST_LEAQ("0x4000(%%rbx)");
	TEST_LEAQ("0x4000(%%rcx)");
	TEST_LEAQ("0x4000(%%rdx)");
	TEST_LEAQ("0x4000(%%rsi)");
	TEST_LEAQ("0x4000(%%rdi)");

	TEST_LEAQ("(%%rax, %%rcx)");
	TEST_LEAQ("(%%rbx, %%rdx)");
	TEST_LEAQ("(%%rcx, %%rcx)");
	TEST_LEAQ("(%%rdx, %%rcx)");
	TEST_LEAQ("(%%rsi, %%rcx)");
	TEST_LEAQ("(%%rdi, %%rcx)");

	TEST_LEAQ("0x40(%%rax, %%rcx)");
	TEST_LEAQ("0x4000(%%rbx, %%rdx)");

	TEST_LEAQ("(%%rcx, %%rcx, 2)");
	TEST_LEAQ("(%%rdx, %%rcx, 4)");
	TEST_LEAQ("(%%rsi, %%rcx, 8)");

	TEST_LEAQ("(,%%rax, 2)");
	TEST_LEAQ("(,%%rbx, 4)");
	TEST_LEAQ("(,%%rcx, 8)");

	TEST_LEAQ("0x40(,%%rax, 2)");
	TEST_LEAQ("0x40(,%%rbx, 4)");
	TEST_LEAQ("0x40(,%%rcx, 8)");

	TEST_LEAQ("-10(%%rcx, %%rcx, 2)");
	TEST_LEAQ("-10(%%rdx, %%rcx, 4)");
	TEST_LEAQ("-10(%%rsi, %%rcx, 8)");

	TEST_LEAQ("0x4000(%%rcx, %%rcx, 2)");
	TEST_LEAQ("0x4000(%%rdx, %%rcx, 4)");
	TEST_LEAQ("0x4000(%%rsi, %%rcx, 8)");
	return 0;
}
