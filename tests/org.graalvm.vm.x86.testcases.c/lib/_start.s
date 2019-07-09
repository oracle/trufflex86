#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
	.global	_start
	.hidden	_start
	.type	_start,@function
_start:
	xor	%rbp,	%rbp
	mov	%rsp,	%rdi
	call	_main
	.size	_start,	.-_start
	.end	_start
