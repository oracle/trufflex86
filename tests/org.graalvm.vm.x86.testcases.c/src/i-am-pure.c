/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>

int i_am_pure(char *c, int n)
{
	char *d = __builtin_alloca(n);
	int i;
	int sum;
	for(i = 0; i < n; i++)
		d[i] = c[i];
	for(i = 0; i < n; i++)
		d[i] *= c[n - i];
	for(i = 0; i < n; i++)
		sum += d[i];
	if(sum)
		__builtin_unreachable();
	return sum;
}

char array[11];
int main(void)
{
	printf("%d\n", i_am_pure(array,5));
	printf("%d\n", i_am_pure(array,11));
	return 0;
}
