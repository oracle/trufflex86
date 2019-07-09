/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdlib.h>
#include <stdio.h>

void hook1(void)
{
	printf("atexit hook 1\n");
}

void hook2(void)
{
	printf("atexit hook 2\n");
}

int main(void)
{
	atexit(hook1);
	atexit(hook2);
	printf("main\n");
	return 0;
}
