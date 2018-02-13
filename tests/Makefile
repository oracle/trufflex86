ELF_TARGETS := $(TESTS:%=%.elf)

QUIET	:= @

CC	:= gcc
LD	:= ld
CFLAGS	:= -fPIE -O3 -g -std=c99 -pedantic -Wall -fdata-sections -ffunction-sections
LDFLAGS	:= -fPIE -pie -O3 -Wl,--gc-sections

CCNOSTD	:= -ffreestanding -fno-stack-protector -static -nostdlib
LDNOSTD	:= $(CCNOSTD) -Wl,-y,__stack_chk_fail

.PHONY: all

all: $(ELF_TARGETS)

# nostdlib
%.o: %.c
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[CC]   $@"
	$(QUIET)$(CC) $(CFLAGS) $(CCNOSTD) -c -o "$@" "$<"

%.elf: %.o lib/_start.o lib/libc.o
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[CCLD] $@"
	$(QUIET)$(CC) $(LDFLAGS) $(LDNOSTD) -o "$@" lib/_start.o lib/libc.o "$<"

# assembler
%.o: %.s
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[AS]   $@"
	$(QUIET)$(CC) -c -o "$@" "$<"

%.elf: %.o
	$(QUIET)mkdir -p $(shell dirname $@)
	$(QUIET)echo "[LD]   $@"
	$(QUIET)$(LD) -o "$@" "$<"
