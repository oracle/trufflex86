/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <string.h>
#include <sys/syscall.h>

int main(void)
{
	int fd = 1;
	char* buf = "Hello world!\n";
	int count = strlen(buf);
	__asm__("syscall" : : "a"(SYS_write), "D"(fd), "S"(buf), "d"(count));
	return 0;
}
