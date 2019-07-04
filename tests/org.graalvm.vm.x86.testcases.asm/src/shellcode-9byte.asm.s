#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
	.text
	.global	_start

_start:	# init code
	xor	%rax,	%rax
	xor	%rbx,	%rbx
	xor	%rcx,	%rcx
	xor	%rdx,	%rdx
	xor	%rsi,	%rsi
	xor	%rdi,	%rdi
	push	$0x246
	popf

	# BEGIN
shell:	syscall
	add	%ecx,	%edx
	push	%rcx
	pop	%rsi
	lodsb
	syscall
	# END

exit:	mov	$60,	%eax
	xor	%edi,	%edi
	syscall

text:	.ascii	"Hello world!"
