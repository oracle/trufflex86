#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

int string_arg(const char* str)
{
	return atof(str);
}

const char* string_ret_const()
{
	return "Hello, World!";
}

struct dynamic_string {
	int magic;
	char str[16];
};

char* string_ret_dynamic(int nr)
{
	struct dynamic_string *alloc = malloc(sizeof(*alloc));
	alloc->magic = nr;
	snprintf(alloc->str, sizeof(alloc->str), "%d", nr);
	return alloc->str;
}

// wrapper around "free" that has a return value that can be verified
int free_dynamic_string(char* str)
{
	struct dynamic_string* dynamic = NULL;
	intptr_t offset = dynamic->str - (char*) dynamic;
	dynamic = (struct dynamic_string*) (str - offset);
	int magic = dynamic->magic;
	free(dynamic);
	return magic;
}

int string_callback(int (*str_arg)(const char*), char *(*str_ret)())
{
	int ret;
	char* str = str_ret();
	if(str && strcmp(str, "Hello, Native!") == 0) {
		ret = str_arg("Hello, Truffle!");
	} else {
		ret = 0;
	}
	free(str);
	return ret;
}

const char* native_string_callback(const char *(*str_ret)())
{
	const char* str = str_ret();
	if(!str) {
		return "null";
	} else if(str == string_ret_const()) {
		return "same";
	} else {
		return "different";
	}
}
