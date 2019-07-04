/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include "nanolibc.h"

int main(void)
{
	gid_t gid = getgid();
	printf("gid: %d\n", gid);
	return 0;
}
