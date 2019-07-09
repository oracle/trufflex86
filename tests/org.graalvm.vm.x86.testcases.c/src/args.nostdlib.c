/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#define _GNU_SOURCE
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/syscall.h>

int main(int argc, char** argv)
{
	int i;
	printf("Arguments: %d\n", argc);
	for(i = 0; i < argc; i++) {
		printf("args[%d] = '%s'\n", i, argv[i]);
	}
	syscall(60, argc);
	return 0;
}
