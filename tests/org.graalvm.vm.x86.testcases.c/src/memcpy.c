#include <string.h>
#include <stdio.h>

const char* TEXT = "Hello world!";

int main(void)
{
	char buf[256];
	memcpy(buf, TEXT, strlen(TEXT) + 1);
	puts(buf);
	return 0;
}
