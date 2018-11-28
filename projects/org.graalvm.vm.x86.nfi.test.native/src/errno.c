#include <errno.h>

int getErrno()
{
	return errno;
}

void setErrno(int value)
{
	errno = value;
}

int errnoCallback(int value, void (*callback)())
{
	errno = value;
	callback();
	return errno;
}
