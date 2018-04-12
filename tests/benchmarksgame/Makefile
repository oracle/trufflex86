CC	:=	gcc
CFLAGS	:=	-static -O3

.PHONY: all clean

all: bin/binarytrees.gcc bin/binarytrees.gcc-2 bin/fasta.cint bin/fasta.gcc bin/fasta.gcc-4 bin/fannkuchredux.cint bin/fannkuchredux.gcc bin/fannkuchredux.gcc-3 bin/mandelbrot.cint bin/mandelbrot.gcc-2 bin/mandelbrot.gcc-4 bin/mandelbrot.gcc-8 bin/mandelbrot.gcc-9

clean:
	rm -f bin/binarytrees.gcc bin/binarytrees.gcc-2 bin/fasta.cint bin/fasta.gcc bin/fasta.gcc-4 bin/fannkuchredux.cint bin/fannkuchredux.gcc bin/fannkuchredux.gcc-3 bin/mandelbrot.cint bin/mandelbrot.gcc-2 bin/mandelbrot.gcc-4 bin/mandelbrot.gcc-8 bin/mandelbrot.gcc-9

bin:
	mkdir bin

bin/binarytrees.gcc: binarytrees/binarytrees.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $< -lm

bin/binarytrees.gcc-2: binarytrees/binarytrees.gcc-2.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

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

bin/mandelbrot.cint: mandelbrot/mandelbrot.cint-2.cint.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gcc-2: mandelbrot/mandelbrot.gcc-2.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gcc-4: mandelbrot/mandelbrot.gcc-4.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gcc-8: mandelbrot/mandelbrot.gcc-8.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gcc-9: mandelbrot/mandelbrot.gcc-9.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<
