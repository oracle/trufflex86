#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
	.text
	.global _start

_start:	xor	%eax,	%eax
	xor	%ebx,	%ebx
	xor	%ecx,	%ecx
	xor	%edx,	%edx
	xor	%esi,	%esi
	xor	%edi,	%edi

	syscall

	mov	%rcx,	%rdi
	mov	$60,	%eax
	syscall
