#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
.PHONY: all clean

all: helloworld.elf

clean:
	rm -rf *.o *.elf

%.o: %.s
	@echo "[AS]  $<"
	@gcc -c -o "$@" "$<"

%.elf: %.o
	@echo "[LD]  $<"
	@ld -o "$@" "$<"
