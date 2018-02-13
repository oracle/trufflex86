	.global	_start
	.hidden	_start
	.type	_start,@function
_start:
	xor	%rbp,	%rbp
	mov	%rsp,	%rdi
	call	_main
	.end	_start
	.size	_start,	.-_start
