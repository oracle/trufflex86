#include <stdio.h>
#include <string.h>
#include <unistd.h>

const char* text = "Hello world!\n";

int puts(const char* s)
{
	return write(1, s, strlen(s));
}

int main(void)
{
	puts(text);
	return 1;
}
