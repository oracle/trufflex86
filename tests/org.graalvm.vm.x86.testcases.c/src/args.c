#define _GNU_SOURCE
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/syscall.h>

int main(int argc, char** argv)
{
	int i;
	printf("Arguments: %d\n", argc);
	for(i = 0; i < argc; i++) {
		printf("args[%d] = '%s'\n", i, argv[i]);
	}
	syscall(60, 42L);
	return 0;
}
