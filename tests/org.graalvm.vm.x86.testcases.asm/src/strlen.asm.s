#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
	.text

        .global	_start
_start:
	# strlen("Hello world!\n")
	mov	$msg,	%rdi
	call	strlen
	mov	%rax,	%rdx

	# write(1, "Hello world!\n", 13)
	xor	%eax,	%eax
	inc	%eax
	mov	%eax,	%edi
	mov	$msg,	%rsi
	# mov	$13,	%edx
	syscall

	# exit(0)
	mov	$60,	%eax
	xor	%edi,	%edi
	syscall

strlen:	mov	%rdi,	%rsi
.L0:	lodsb
	test	%al,	%al
	jnz	.L0
	mov	%rsi,	%rax
	sub	%rdi,	%rax
	dec	%rax
	ret

	.data
msg:	.asciz	"Hello world!\n"
