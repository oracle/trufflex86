/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <stdio.h>
#include <stdlib.h>

#define TESTCASE(x) { \
	printf("Running test case \"%s\"\n", #x); \
	if(!(x)) { \
		printf("ERROR: \"%s\" failed\n", #x); \
		return 1; \
	} \
}

#define EXIT(file) { \
	fclose(file); \
	printf("EXIT()\n"); \
	return 0; \
}

char buf[4];

int main(void)
{
	//char* buffer = (char*)malloc(4);
	char* buffer = buf;
	FILE* fh;
	TESTCASE((fh = tmpfile()) != NULL);
	TESTCASE(setvbuf(fh, buffer, _IOLBF, 4) == 0);
	/* Testing ungetc() at offset 0 */
	//rewind(fh);
	TESTCASE(fseek(fh, 0L, SEEK_SET) == 0);
	TESTCASE(ungetc('x', fh) == 'x');
	TESTCASE(ftell(fh) == -1l);
	//rewind(fh);
	TESTCASE(fseek(fh, 0L, SEEK_SET) == 0);
#if 0
	TESTCASE(ftell(fh) == 0l);
	/* Commence "normal" tests */
#endif
	TESTCASE(fputc('1', fh) == '1');
EXIT(fh);
	TESTCASE(fputc('2', fh) == '2');
	TESTCASE(fputc('3', fh) == '3');
	/* Positions incrementing as expected? */
	TESTCASE(ftell(fh) == 3l);
	/* Buffer properly flushed when full? */
	TESTCASE(fputc('4', fh) == '4');
	/* fflush() resetting positions as expected? */
	TESTCASE(fputc('5', fh) == '5');
	TESTCASE(fflush(fh) == 0);
	TESTCASE(ftell(fh) == 5l);
	/* rewind() resetting positions as expected? */
	rewind(fh );
	TESTCASE(ftell(fh) == 0l);
	/* Reading back first character after rewind for basic read check */
	TESTCASE(fgetc(fh) == '1');
	/* TODO: t.b.c. */
	TESTCASE(fclose(fh) == 0);
	return 0;
}
