/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#define _GNU_SOURCE

#include <limits.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/mman.h>

const char* message = "Hello world!\n";

int main(void)
{
	size_t result;
	void* ptr = mmap(NULL, 4096, PROT_READ | PROT_WRITE, MAP_ANONYMOUS | MAP_PRIVATE, -1, 0);
	if(!ptr)
		abort();

	strcpy(ptr, message);
	result = write(1, ptr, 8192);
	return (result == 4096) ? 0 : 1;
}
