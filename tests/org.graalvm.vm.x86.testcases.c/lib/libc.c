/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
/* vim:set ts=8 sts=8 sw=8 tw=80 cc=80 noet: */
#include <string.h>
#include <stdint.h>
#include <stdarg.h>
#include <stdio.h>
#include <sys/uio.h>
#include <sys/utsname.h>
#include <sys/syscall.h>

int errno;

extern int main(int, char**, char**);

void exit(int status);

#define asm __asm__
#define inline __inline__

// syscall helpers
#define __SYSCALL_0(result, id)						       \
	__asm__ volatile("syscall" : "=a"(result)			       \
				   : "a"(id)				       \
				   : "memory", "rcx", "r11");

#define __SYSCALL_1(result, id, a1)					       \
	__asm__ volatile("syscall" : "=a"(result)			       \
				   : "a"(id), "D"(a1)			       \
				   : "memory", "rcx", "r11");

#define __SYSCALL_2(result, id, a1, a2)					       \
	__asm__ volatile("syscall" : "=a"(result)			       \
				   : "a"(id), "D"(a1), "S"(a2)		       \
				   : "memory", "rcx", "r11");

#define __SYSCALL_3(result, id, a1, a2, a3)				       \
	__asm__ volatile("syscall" : "=a"(result)			       \
				   : "a"(id), "D"(a1), "S"(a2), "d"(a3)	       \
				   : "memory", "rcx", "r11");

#define __SYSCALL_6(result, id, a1, a2, a3, a4, a5, a6) {		       \
	register int64_t r10 asm("r10") = a4;				       \
	register int64_t r8 asm("r8") = a5;				       \
	register int64_t r9 asm("r9") = a6;				       \
	__asm__ volatile("syscall" : "=a"(result)			       \
				   : "a"(id), "D"(a1), "S"(a2), "d"(a3),       \
				     "r"(r10), "r"(r8), "r"(r9)		       \
				   : "memory", "rcx", "r11");		       \
}

#define __SYSCALL_RET(result) {						       \
	if(result < 0) {						       \
		errno = -result;					       \
		return -1;						       \
	}								       \
	return result;							       \
}

#define __SYSCALL_0P(id) {						       \
	int64_t result;							       \
	__SYSCALL_0(result, id);					       \
	__SYSCALL_RET(result);						       \
}

#define __SYSCALL_1P(id, a1) {						       \
	int64_t result;							       \
	__SYSCALL_1(result, id, a1);					       \
	__SYSCALL_RET(result);						       \
}

#define __SYSCALL_2P(id, a1, a2) {					       \
	int64_t result;							       \
	__SYSCALL_2(result, id, a1, a2);				       \
	__SYSCALL_RET(result);						       \
}

#define __SYSCALL_3P(id, a1, a2, a3) {					       \
	int64_t result;							       \
	__SYSCALL_3(result, id, a1, a2, a3);				       \
	__SYSCALL_RET(result);						       \
}

#define __SYSCALL_6P(id, a1, a2, a3, a4, a5, a6) {			       \
	int64_t result;							       \
	__SYSCALL_6(result, id, a1, a2, a3, a4, a5, a6);		       \
	__SYSCALL_RET(result);						       \
}

// posix/libc functions
ssize_t read(int fd, void* buf, size_t count)
{
	__SYSCALL_3P(__NR_read, fd, buf, count);
}

ssize_t write(int fd, const void* buf, size_t count)
{
	__SYSCALL_3P(__NR_write, fd, buf, count);
}

int open(const char* filename, int flags, mode_t mode)
{
	__SYSCALL_3P(__NR_open, filename, flags, mode);
}

int close(int fd)
{
	__SYSCALL_1P(__NR_close, fd);
}

long lseek(int fd, off_t offset, int whence)
{
	__SYSCALL_3P(__NR_lseek, fd, offset, whence);
}

ssize_t readv(int fd, const struct iovec* iov, int iovcnt)
{
	__SYSCALL_3P(__NR_readv, fd, iov, iovcnt);
}

ssize_t writev(int fd, const struct iovec* iov, int iovcnt)
{
	__SYSCALL_3P(__NR_writev, fd, iov, iovcnt);
}

char* getcwd(char* buf, size_t size)
{
	int64_t result;
	__SYSCALL_2(result, __NR_getcwd, buf, size);
	if(result < 0) {
		errno = -result;
		return NULL;
	}
	return buf;
}

int mkdir(const char* path, mode_t mode)
{
	__SYSCALL_2P(__NR_mkdir, path, mode);
}

int rmdir(const char* path, mode_t mode)
{
	__SYSCALL_2P(__NR_rmdir, path, mode);
}

int uname(struct utsname* buf)
{
	__SYSCALL_1P(__NR_uname, buf);
}

int getuid(void)
{
	__SYSCALL_0P(__NR_getuid);
}

int getgid(void)
{
	__SYSCALL_0P(__NR_getgid);
}

void _main(long* p)
{
	long argc = *(p++);
	char** argv = (char**) p;
	char** envp = (char**) (argv + argc + 1);
	exit(main(argc, argv, envp));
}

void exit(int status)
{
	long result;
	__SYSCALL_1(result, __NR_exit, (long) status);
	for(;;) {
		__SYSCALL_1(result, __NR_exit, (long) status);
	}
}

void _Exit(int ec)
{
	int64_t result;
	__SYSCALL_1(result, __NR_exit_group, ec);
}

size_t strlen(const char* s)
{
	char* p;
	for(p = (char*) s; *p; p++);
	return p - s;
}

int puts(const char* s)
{
	size_t len = strlen(s);
	size_t written = write(1, s, len);
	write(1, "\n", 1);
	return written + 1;
}

////////////////////////////////////////////////////////////////////////////////
static char* __sprinth(char* result, uint32_t x) // result must be char[9]
{
	const char* letters = "0123456789ABCDEF";
	char* p = &result[sizeof(result) - 1];
	*p = 0;
	if(x == 0) {
		*(--p) = '0';
		return p;
	}
	while(x != 0) {
		*(--p) = letters[x & 0x0F];
		x >>= 4;
	}
	return p;
}

static char* __sprintd(char* result, uint32_t x) // result must be char[11]
{
	char* p = &result[sizeof(result) - 1];
	*p = 0;
	if(x == 0) {
		*(--p) = '0';
		return p;
	}
	while(x != 0) {
		*(--p) = (x % 10) + 0x30;
		x /= 10;
	}
	return p;
}

int vsprintf(char* buf, const char* s, va_list args)
{
	char tmp[16];
	char* c;
	char* p = buf;
	int n;
	char pad = 0;
	int t;
	for(; *s != 0; s++) {
		if(*s == '%') {
			pad = 0;
			n = 0;
			s++;
			if(*s == '0') {
				s++;
				pad = '0';
			}
			while((*s >= '0') && (*s <= '9')) {
				n *= 10;
				n += *s - '0';
				s++;
				if(!pad)
					pad = ' ';
			}
			if(*s == 'd') {
				int val = va_arg(args, int);
				c = __sprintd(tmp, val);
				t = n - strlen(c);
				if(pad)
					for(; t > 0; t--)
						*(p++) = pad;
				for(; *c != 0; c++)
					*(p++) = *c;
			} else if(*s == 'x') {
				int val = va_arg(args, int);
				c = __sprinth(tmp, val);
				t = n - strlen(c);
				if(pad)
					for(; t > 0; t--)
						*(p++) = pad;
				for(; *c != 0; c++)
					*(p++) = *c | 0x20;
			} else if(*s == 'X') {
				int val = va_arg(args, int);
				c = __sprinth(tmp, val);
				t = n - strlen(c);
				if(pad)
					for(; t > 0; t--)
						*(p++) = pad;
				for(; *c != 0; c++)
					*(p++) = *c;
			} else if(*s == 's') {
				c = (char*)va_arg(args, char*);
				t = n - strlen(c);
				if(pad)
					for(; t > 0; t--)
						*(p++) = ' ';
				for(; *c != 0; c++)
					*(p++) = *c;
			}
		} else
			*(p++) = *s;
	}
	*(p++) = 0;
	return p - buf - 1;
}

int sprintf(char* buf, const char* s, ...)
{
	va_list args;
	int result;
	va_start(args, s);
	result = vsprintf(buf, s, args);
	va_end(args);
	return result;
}

int printf(const char* format, ...)
{
	char buf[256];
	va_list args;
	int result;
	va_start(args, format);
	result = vsprintf(buf, format, args);
	write(1, buf, result);
	va_end(args);
	return result;
}
