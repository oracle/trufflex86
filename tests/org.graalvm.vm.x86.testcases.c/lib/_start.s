	.global	_start
	.hidden	_start
	.type	_start,@function
_start:
	xor	%rbp,	%rbp
	mov	%rsp,	%rdi
	call	_main
	.size	_start,	.-_start
	.end	_start
