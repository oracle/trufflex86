	.text
	.global	_start

_start:	mov	$1,	%eax
	xor	%edi,	%edi
	lea	text(%rip),%rsi
	mov	$18,	%edx
	syscall

	mov	$60,	%eax
	xor	%rdi,	%rdi
	syscall

text:	.ascii	"this is hot shit!\n"
