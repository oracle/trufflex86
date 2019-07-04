/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#define _XOPEN_SOURCE
#include <stdio.h>
#include <unistd.h>

int main(void)
{
	printf("uid=%d, gid=%d\n", getuid(), getgid());
	return 0;
}
