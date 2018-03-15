#include <stdlib.h>
#include <unistd.h>

int main(long p)
{
	write(1, "Hello world!\n", 13);
	exit(0);
}
