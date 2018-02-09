	.text

        .global	_start
_start:
	# write(1, "Hello world!\n", 13)
	xor	%eax,	%eax
	inc	%eax
	mov	%eax,	%edi
	mov	$msg,	%rsi
	mov	$13,	%edx
	syscall

	# exit(0)
	mov	$60,	%eax
	xor	%edi,	%edi
	syscall

	.data
msg:	.ascii	"Hello world!\n"
