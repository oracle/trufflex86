#include <stdio.h>
#include "qemu-common.h"

int main(void)
{
	long res;
	/* specific popl test */
	asm volatile ("push $12345432 ; push $0x9abcdef ; pop (%%rsp) ; pop %0"
			: "=g" (res));
	printf("popl esp=" FMTLX "\n", res);
}
