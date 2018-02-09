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
