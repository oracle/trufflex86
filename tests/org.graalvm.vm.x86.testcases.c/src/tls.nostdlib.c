/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#define _GNU_SOURCE

#include <stdio.h>
#include <stdint.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <asm/prctl.h>
#include <sys/prctl.h>

static long get_fs_selector(void)
{
	long fs;
	__asm__ volatile ("push %%fs; pop %[fs]" : [fs] "=r"(fs));
	return fs;
}

static long get_fs(void)
{
	long fs;
	syscall(__NR_arch_prctl, ARCH_GET_FS, &fs);
	return fs;
}

static void set_fs(void* fs)
{
	syscall(__NR_arch_prctl, ARCH_SET_FS, fs);
}

int main(void)
{
	long tls;
	char* ptr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	printf("fs(selector)=%x\n", get_fs_selector());
	printf("fs=%x\n", get_fs());

	/* write fs */
	set_fs(ptr);

	printf("fs(selector)=%x\n", get_fs_selector());
	printf("fs=%x\n", get_fs());

	/* read from fs:0 */
	__asm__ volatile ("movq %%fs:0, %[tls]" : [tls] "=r"(tls));
	printf("tls=%x%x\n", (int) (tls >> 32), (int) (tls));
	return 0;
}
