#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
	.text

        .global	_start

_start:
	call	main

	.global	main
	.type	main,@function
	.func	main
main:
	call	has_cpuid

	cmp	$0,	%rax
	je	0f
	cmp	$1,	%rax
	je	1f
	cmp	$2,	%rax
	je	2f

	mov	$msgunk,%rdi
	jmp	3f

0:	mov	$msg386,%rdi
	jmp	3f

1:	mov	$msg486,%rdi
	jmp	3f

2:	mov	$msg586,%rdi
	jmp	3f

3:	call	write
	call	exit
	.size	main,.-main
	.endfunc

	.global write
	.type   write,@function
	.func	write
write:	push	%rdi
	call	strlen
	mov	%rax,	%rdx
	pop	%rsi
	xor	%eax,	%eax
	inc	%eax
	mov	%eax,	%edi
	syscall
	ret
	.size	write,.-write
	.endfunc

	# exit(0)
	.global	exit
	.type	exit,@function
	.func	exit
exit:	mov	$60,	%eax
	xor	%edi,	%edi
	syscall
	.size	exit,.-exit
	.endfunc

	.global	strlen
	.type	strlen,@function
	.func	strlen
strlen:	mov	%rdi,	%rsi
0:	lodsb
	test	%al,	%al
	jnz	0b
	mov	%rsi,	%rax
	sub	%rdi,	%rax
	dec	%rax
	ret
	.size	strlen,.-strlen
	.endfunc

	.global	has_cpuid
	.type	has_cpuid,@function
	.func	has_cpuid
has_cpuid:
	pushf
	pop	%rax
	push	%rax
	mov	%rax,	%rcx
	xor	$0x40000,%rax
	push	%rax
	popf
	pushf
	pop	%rax
	popf
	cmp	%rax,	%rcx
	jne	0f			# detect_i486
	# is i386
	xor	%rax,	%rax
	ret

	# detect_i486
0:	mov	%rcx,	%rax
	xor	$0x200000, %rax
	push	%rax
	popf
	pushf
	pop	%rax
	cmp	%rax,	%rcx
	jne	0f			# detect_i586
	# is i486
	xor	%rax,	%rax
	inc	%rax
	ret

	# detect_i586
0:	xor	%rax,	%rax
	cpuid
	or	%rax,	%rax
	je	0f			# cpu486

	mov	$2,	%rax
	ret

	# cpu486
0:	mov	$1,	%rax
	ret

	.size	has_cpuid,.-has_cpuid
	.endfunc

	.data
msg386:	.asciz	"i386\n"
msg486:	.asciz	"i486\n"
msg586:	.asciz	"i586\n"
msgunk:	.asciz	"unknown\n"
