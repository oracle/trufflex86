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
