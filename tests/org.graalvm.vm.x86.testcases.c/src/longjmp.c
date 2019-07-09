/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>
#include <setjmp.h>

jmp_buf buf;

void call(void)
{
	printf("call\n");
	printf("calling longjmp\n");
	longjmp(buf, 1);
}

int main(void)
{
	printf("Start\n");
	if(!setjmp(buf)) {
		printf("zero\n");
		longjmp(buf, 1);
		printf("after longjmp\n");
	} else {
		printf("non-zero\n");
	}
	if(!setjmp(buf)) {
		printf("another setjmp\n");
		call();
		printf("call returned\n");
	} else {
		printf("exception!\n");
	}
	if(!setjmp(buf)) {
		printf("yet another setjmp\n");
	} else {
		printf("exception 2!\n");
	}
	return 0;
}
