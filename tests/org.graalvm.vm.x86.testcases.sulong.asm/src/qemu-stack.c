/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
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
