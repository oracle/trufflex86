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
