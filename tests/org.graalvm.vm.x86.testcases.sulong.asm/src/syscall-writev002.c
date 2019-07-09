/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include "nanolibc.h"

int main(void)
{
	char* str0 = "hello ";
	char* str1 = "world\n";
	struct iovec iov[2];
	ssize_t result;

	iov[0].iov_base = str0;
	iov[0].iov_len = strlen(str0);
	iov[1].iov_base = str1;
	iov[1].iov_len = strlen(str1);

	result = writev(STDOUT_FILENO, iov, -1);

	printf("written: %ld\n", result);
	if(result < 0) {
		printf("errno: %d\n", errno);
		return 0;
	}

	return 1;
}
