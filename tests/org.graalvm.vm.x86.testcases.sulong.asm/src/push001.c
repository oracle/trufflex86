/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
int main(void)
{
	unsigned short value = 0;
	__asm__("pushw $0x1234; popw %0" : "=r"(value));
	return value == 0x1234;
}
