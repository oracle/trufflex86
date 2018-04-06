#include <stdio.h>
#include "flags.h"

int main(void)
{
	char flags;
	__asm__("movb $0xFF, %%al\n\t"
		"cmpb $0x02, %%al\n\t"
		"lahf\n\t"
		"movb %%ah, %%al" : "=a"(flags));
	printf("%02X\n", flags & CC_MASK8);
}
