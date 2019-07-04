/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	unsigned long ptr = 0;
	unsigned long out = 0;
	__asm__("lea %1, %0" : "=r"(out) : "m"(ptr));
	return &ptr == (unsigned long *)out;
}
