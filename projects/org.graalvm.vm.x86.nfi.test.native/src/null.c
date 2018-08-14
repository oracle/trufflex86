#include <stdlib.h>

void* return_null()
{
	return NULL;
}

const char* null_arg(void* arg)
{
	if(!arg) {
		return "null";
	} else {
		return "non-null";
	}
}

void callback_null_arg(void (*callback)(void*))
{
	callback(NULL);
}

const char* callback_null_ret(void *(*callback)())
{
	void* ret = callback();
	if(!ret) {
		return "null";
	} else {
		return "non-null";
	}
}
