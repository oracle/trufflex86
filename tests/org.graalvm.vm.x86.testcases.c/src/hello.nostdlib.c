/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdlib.h>
#include <unistd.h>

int main(long p)
{
	write(1, "Hello world!\n", 13);
	exit(0);
}
