CC	:=	gcc
CFLAGS	:=	-static -O3

.PHONY: all clean

all: bin/fasta.cint bin/fasta.gcc bin/fasta.gcc-4 bin/fannkuchredux.cint bin/fannkuchredux.gcc bin/fannkuchredux.gcc-3

clean:
	rm -f bin/fasta.cint bin/fasta.gcc bin/fasta.gcc-4 bin/fannkuchredux.cint bin/fannkuchredux.gcc bin/fannkuchredux.gcc-3

bin:
	mkdir bin

bin/fasta.cint: fasta/fasta.cint.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/fasta.gcc: fasta/fasta.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/fasta.gcc-4: fasta/fasta.gcc-4.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/fannkuchredux.cint: fannkuchredux/fannkuchredux.cint.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/fannkuchredux.gcc: fannkuchredux/fannkuchredux.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/fannkuchredux.gcc-3: fannkuchredux/fannkuchredux.gcc-3.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<
