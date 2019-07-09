/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>
#include <sys/syscall.h>

int main(void)
{
	long error = 0;
	char buf[257];
	__asm__("syscall"
		: "=a"(error)
		: "a"(SYS_getcwd), "D"(buf), "S"(sizeof(buf)));
	printf("len: %ld\n", error);
	if(error < 0) {
		return 1;
	}
	printf("value: '%s'\n", buf);
	return 0;
}
