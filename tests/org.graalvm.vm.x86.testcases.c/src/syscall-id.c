#define _XOPEN_SOURCE
#include <stdio.h>
#include <unistd.h>

int main(void)
{
	printf("uid=%d, gid=%d\n", getuid(), getgid());
	return 0;
}
