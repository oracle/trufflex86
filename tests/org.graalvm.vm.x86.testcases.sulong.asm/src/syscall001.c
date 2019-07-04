/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <string.h>
#include "nanolibc.h"

int main(void)
{
	char* buf = "Hello world!\n";
	int count = strlen(buf);
	syscall(SYS_write, 1, buf, count);
	syscall(SYS_exit, 42);
}
