#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
	.global	syscall
	.type	syscall,@function
syscall:
	movq	%rdi,%rax
	movq	%rsi,%rdi
	movq	%rdx,%rsi
	movq	%rcx,%rdx
	movq	%r8,%r10
	movq	%r9,%r8
	movq	8(%rsp),%r9
	syscall
	ret
	.size	syscall,.-syscall
	.end	syscall
