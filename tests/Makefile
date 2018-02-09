ELF_TARGETS := $(TESTS:%=%.elf)

CC := gcc
LD := ld

.PHONY: all

all: $(ELF_TARGETS)

%.o: %.s
	@mkdir -p $(shell dirname $@)
	@echo "[AS]  $@"
	@$(CC) -c -o "$@" "$<"

%.elf: %.o
	@mkdir -p $(shell dirname $@)
	@echo "[LD]  $@"
	@$(LD) -o "$@" "$<"
