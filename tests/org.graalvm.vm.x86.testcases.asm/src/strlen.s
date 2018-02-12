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
