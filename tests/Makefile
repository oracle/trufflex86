#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
ELF_TARGETS := $(TESTS:%=%.elf)

QUIET	:= @

CC	:= gcc
LD	:= ld

CFLAGS	:= -fPIE -O3 -g -std=c99 -pedantic -Wall -fdata-sections -ffunction-sections
LDFLAGS	:= -fPIE -pie -O3 -Wl,--gc-sections

CCNOSTD	:= -ffreestanding -fno-stack-protector -static -nostdlib
LDNOSTD	:= $(CCNOSTD) -Wl,-y,__stack_chk_fail

CCSTATIC:= -static
LDSTATIC:= $(CCSTATIC)

.PHONY: all

all: $(ELF_TARGETS)

# nostdlib
%.nostdlib.o: %.nostdlib.c
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[CC-NSTD]   $@"
	$(QUIET)$(CC) $(CFLAGS) $(CCNOSTD) -c -o "$@" "$<"

lib/libc.o: lib/libc.c
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[CC-NSTD]   $@"
	$(QUIET)$(CC) $(CFLAGS) $(CCNOSTD) -c -o "$@" "$<"

%.nostdlib.elf: %.nostdlib.o lib/_start.o lib/syscall.o lib/libc.o
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[CCLD-NSTD] $@"
	$(QUIET)$(CC) $(LDFLAGS) $(LDNOSTD) -o "$@" lib/_start.o lib/libc.o lib/syscall.o "$<"

# assembler
%.o: %.s
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[AS]        $@"
	$(QUIET)$(CC) -c -o "$@" "$<"

%.asm.elf: %.asm.o
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[LD-ASM]    $@"
	$(QUIET)$(LD) -o "$@" "$<"

# glibc static
%.o: %.c
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[CC]        $@"
	$(QUIET)$(CC) $(CFLAGS) $(CCSTATIC) -c -o "$@" "$<"

%.elf: %.o
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(LDFLAGS) $(LDSTATIC) -o "$@" "$<" -lm
