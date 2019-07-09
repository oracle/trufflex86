/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <fcntl.h>

const char* letters = "0123456789ABCDEF";

/* auxv definitions */
#define AT_NULL         0               /* End of vector */
#define AT_IGNORE       1               /* Entry should be ignored */
#define AT_EXECFD       2               /* File descriptor of program */
#define AT_PHDR         3               /* Program headers for program */
#define AT_PHENT        4               /* Size of program header entry */
#define AT_PHNUM        5               /* Number of program headers */
#define AT_PAGESZ       6               /* System page size */
#define AT_BASE         7               /* Base address of interpreter */
#define AT_FLAGS        8               /* Flags */
#define AT_ENTRY        9               /* Entry point of program */
#define AT_NOTELF       10              /* Program is not ELF */
#define AT_UID          11              /* Real uid */
#define AT_EUID         12              /* Effective uid */
#define AT_GID          13              /* Real gid */
#define AT_EGID         14              /* Effective gid */
#define AT_CLKTCK       17              /* Frequency of times() */

/* Some more special a_type values describing the hardware.  */
#define AT_PLATFORM     15              /* String identifying platform.  */
#define AT_HWCAP        16              /* Machine-dependent hints about
					                                              processor capabilities.  */

/* This entry gives some information about the FPU initialization
 *    performed by the kernel.  */
#define AT_FPUCW        18              /* Used FPU control word.  */

/* Cache block sizes.  */
#define AT_DCACHEBSIZE  19              /* Data cache block size.  */
#define AT_ICACHEBSIZE  20              /* Instruction cache block size.  */
#define AT_UCACHEBSIZE  21              /* Unified cache block size.  */

/* A special ignored value for PPC, used by the kernel to control the
 *    interpretation of the AUXV. Must be > 16.  */
#define AT_IGNOREPPC    22              /* Entry should be ignored.  */

#define AT_SECURE       23              /* Boolean, was exec setuid-like?  */

#define AT_BASE_PLATFORM 24             /* String identifying real platforms.*/

#define AT_RANDOM       25              /* Address of 16 random bytes.  */

#define AT_HWCAP2       26              /* More machine-dependent hints about
					                                              processor capabilities.  */

#define AT_EXECFN       31              /* Filename of executable.  */

/* Pointer to the global system page used for system calls and other
 *    nice things.  */
#define AT_SYSINFO      32
#define AT_SYSINFO_EHDR 33

/* Shapes of the caches.  Bits 0-3 contains associativity; bits 4-7 contains
 *    log2 of line size; mask those to get cache size.  */
#define AT_L1I_CACHESHAPE       34
#define AT_L1D_CACHESHAPE       35
#define AT_L2_CACHESHAPE        36
#define AT_L3_CACHESHAPE        37

/* auxv names */
const char* auxv_names[38] = {
	/* 00 */ "AT_NULL",
	/* 01 */ "AT_IGNORE",
	/* 02 */ "AT_EXECFD",
	/* 03 */ "AT_PHDR",
	/* 04 */ "AT_PHENT",
	/* 05 */ "AT_PHNUM",
	/* 06 */ "AT_PAGESZ",
	/* 07 */ "AT_BASE",
	/* 08 */ "AT_FLAGS",
	/* 09 */ "AT_ENTRY",
	/* 10 */ "AT_NOTELF",
	/* 11 */ "AT_UID",
	/* 12 */ "AT_EUID",
	/* 13 */ "AT_GID",
	/* 14 */ "AT_EGID",
	/* 15 */ "AT_PLATFORM",
	/* 16 */ "AT_HWCAP",
	/* 17 */ "AT_CLKTK",
	/* 18 */ "AT_FPUCW",
	/* 19 */ "AT_DCACHEBSIZE",
	/* 20 */ "AT_ICACHEBSIZE",
	/* 21 */ "AT_UCACHEBSIZE",
	/* 22 */ "AT_IGNOREPPC",
	/* 23 */ "AT_SECURE",
	/* 24 */ "AT_BASE_PLATFORM",
	/* 25 */ "AT_RANDOM",
	/* 26 */ "AT_HWCAP2",
	/* 27 */ "(27)",
	/* 28 */ "(28)",
	/* 29 */ "(29)",
	/* 30 */ "(30)",
	/* 31 */ "AT_EXECFN",
	/* 32 */ "AT_SYSINFO",
	/* 33 */ "AT_SYSINFO_EHDR",
	/* 34 */ "AT_L1I_CACHESHAPE",
	/* 35 */ "AT_L1D_CACHESHAPE",
	/* 36 */ "AT_L2_CACHESHAPE",
	/* 37 */ "AT_L3_CACHESHAPE"
};

void hexstr8(char* buf, unsigned char val)
{
	int i;
	for(i = 0; i < 2; i++) {
		int x = val & 0xf;
		val >>= 4;
		buf[1 - i] = letters[x];
	}
}

void hexstr32(char* buf, unsigned int val)
{
	int i;
	for(i = 0; i < 8; i++) {
		int x = val & 0xf;
		val >>= 4;
		buf[7 - i] = letters[x];
	}
}

void hexstr64(char* buf, unsigned long val)
{
	hexstr32(buf, (unsigned int) (val >> 32));
	hexstr32(&buf[8], (unsigned int) val);
}

void putk(const char* s)
{
	write(1, s, strlen(s));
}

#if PRINT_MAPS
void cat(const char* filename)
{
	char buf[16];
	int fd;
	ssize_t size;

	fd = open(filename, O_RDONLY, 0);
	if(fd < 0) {
		putk("Cannot open file\n");
		return;
	}
	while(1) {
		size = read(fd, buf, sizeof(buf));
		if(size < 0) {
			putk("Cannot read file\n");
			close(fd);
			return;
		}
		if(size == 0)
			break;
		write(1, buf, size);
	}
	close(fd);
}
#endif

int main(int argc, char** argv, char** envp)
{
	char buf[256];

	char** p;
	int i;
	long* auxv;

	hexstr64(buf, argc);
	buf[16] = '\n';
	buf[17] = 0;
	putk("argc=");
	putk(buf);

	for(i = 0, p = argv; *p; i++, p++) {
		hexstr64(buf, i);
		buf[16] = '=';
		buf[17] = '\'';
		buf[18] = 0;
		putk(buf);
		putk(*p);
		putk("'\n");
	}

	if(i != argc) {
		putk("i != argc\n");
		exit(1);
	}

	for(p = envp; *p; p++);
	if(*p != NULL) {
		putk("Error: *p != NULL\n");
		exit(1);
	}

	auxv = (long*) (p + 1);
	putk("auxv:\n");
	while(*auxv) {
		hexstr64(buf, auxv[0]);
		buf[16] = ' ';
		buf[17] = '(';
		buf[18] = 0;
		putk(buf);
		if(*auxv >= 0 && *auxv < 38)
			putk(auxv_names[*auxv]);
		else
			putk("--");
		buf[0] = ')';
		buf[1] = ' ';
		buf[2] = '=';
		buf[3] = ' ';
		switch(*auxv) {
			case AT_RANDOM: {
				char* random = (char*) auxv[1];
				buf[4] = 0;
				putk(buf);
				for(i = 0; i < 16; i++) {
					hexstr8(buf, random[i]);
					buf[2] = i != 15 ? ' ' : '\n';
					buf[3] = 0;
					putk(buf);
				}
				break;
			}
			case AT_PLATFORM:
			case AT_EXECFN:
				buf[4] = '\'';
				buf[5] = 0;
				putk(buf);
				putk((char*) auxv[1]);
				putk("'\n");
				break;
			default:
				hexstr64(buf + 4, auxv[1]);
				buf[20] = '\n';
				buf[21] = 0;
				putk(buf);
				break;
		}
		auxv += 2;
	}

#if PRINT_MAPS
	cat("/proc/self/maps");
#endif

	return 0;
}
