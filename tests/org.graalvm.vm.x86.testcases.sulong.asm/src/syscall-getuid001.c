/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include "nanolibc.h"

int main(void)
{
	uid_t uid = getuid();
	printf("uid: %d\n", uid);
	return 0;
}
