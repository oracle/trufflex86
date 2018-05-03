CC	:=	gcc
CXX	:=	g++
CFLAGS	:=	-static -O3
CXXFLAGS:=	-static -O3
CFLAGSD	:=	-O3

.PHONY: all clean

all: bin/binarytrees.gcc bin/binarytrees.gcc-2 bin/fasta.cint bin/fasta.gcc \
		bin/fasta.gcc-4 bin/fastaredux.gcc-2 bin/fastaredux.gcc-3 \
		bin/fastaredux.gcc-5 bin/fastaredux.gpp bin/fastaredux.gpp-2 \
		bin/fastaredux.gpp-5 \
		bin/fannkuchredux.cint bin/fannkuchredux.gcc \
		bin/fannkuchredux.gcc-3 bin/mandelbrot.cint \
		bin/mandelbrot.gcc-2 bin/mandelbrot.gcc-4 bin/mandelbrot.gcc-8 \
		bin/mandelbrot.gcc-9 bin/mandelbrot.gpp bin/mandelbrot.gpp-2 \
		bin/mandelbrot.gpp-3 bin/mandelbrot.gpp-5 bin/mandelbrot.gpp-6 \
		bin/mandelbrot.gpp-7 bin/mandelbrot.gpp-8 bin/mandelbrot.gpp-9 \
		bin/spectralnorm.cint bin/spectralnorm.gcc \
		bin/spectralnorm.gcc-2 bin/spectralnorm.gcc-3 \
		bin/spectralnorm.gcc-5 \
		bin/pidigits.cint-4 bin/pidigits.gcc bin/pidigits.gcc-4

clean:
	rm -f bin/binarytrees.gcc bin/binarytrees.gcc-2 bin/fasta.cint \
			bin/fasta.gcc bin/fasta.gcc-4 \
			bin/fastaredux.gcc-2 bin/fastaredux.gcc-3 \
			bin/fastaredux.gcc-5 bin/fastaredux.gpp \
			bin/fastaredux.gpp-2 bin/fastaredux.gpp-5 \
			bin/fannkuchredux.cint \
			bin/fannkuchredux.gcc bin/fannkuchredux.gcc-3 \
			bin/mandelbrot.cint bin/mandelbrot.gcc-2 \
			bin/mandelbrot.gcc-4 bin/mandelbrot.gcc-8 \
			bin/mandelbrot.gcc-9 bin/mandelbrot.gpp \
			bin/mandelbrot.gpp-2 bin/mandelbrot.gpp-3 \
			bin/mandelbrot.gpp-5 bin/mandelbrot.gpp-6 \
			bin/mandelbrot.gpp-7 bin/mandelbrot.gpp-8 \
			bin/mandelbrot.gpp-9 \
			bin/spectralnorm.cint bin/spectralnorm.gcc \
			bin/spectralnorm.gcc-2 bin/spectralnorm.gcc-3 \
			bin/spectralnorm.gcc-5 \
			bin/pidigits.cint-4 bin/pidigits.gcc bin/pidigits.gcc-4

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

bin/fastaredux.gcc-2: fastaredux/fastaredux.gcc-2.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/fastaredux.gcc-3: fastaredux/fastaredux.gcc-3.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/fastaredux.gcc-5: fastaredux/fastaredux.gcc-5.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $<

bin/fastaredux.gpp: fastaredux/fastaredux.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/fastaredux.gpp-2: fastaredux/fastaredux.gpp-2.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/fastaredux.gpp-5: fastaredux/fastaredux.gpp-5.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

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

bin/mandelbrot.gpp: mandelbrot/mandelbrot.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-2: mandelbrot/mandelbrot.gpp-2.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-3: mandelbrot/mandelbrot.gpp-3.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-5: mandelbrot/mandelbrot.gpp-5.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-6: mandelbrot/mandelbrot.gpp-6.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-7: mandelbrot/mandelbrot.gpp-7.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-8: mandelbrot/mandelbrot.gpp-8.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-9: mandelbrot/mandelbrot.gpp-9.gpp.cpp bin
	$(CXX) $(CXXFLAGS) -o $@ $<

bin/spectralnorm.cint: spectralnorm/spectralnorm.cint.c bin
	$(CC) $(CFLAGS) -o $@ $< -lm

bin/spectralnorm.gcc: spectralnorm/spectralnorm.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $< -lm

bin/spectralnorm.gcc-2: spectralnorm/spectralnorm.gcc-2.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $< -lm

bin/spectralnorm.gcc-3: spectralnorm/spectralnorm.gcc-3.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $< -lm

bin/spectralnorm.gcc-5: spectralnorm/spectralnorm.gcc-5.gcc.c bin
	$(CC) $(CFLAGS) -o $@ $< -lm

bin/pidigits.cint-4: pidigits/pidigits.cint-4.cint.c bin
	$(CC) $(CFLAGSD) -o $@ $< -lgmp

bin/pidigits.gcc: pidigits/pidigits.gcc.c bin
	$(CC) $(CFLAGSD) -o $@ $< -lgmp

bin/pidigits.gcc-4: pidigits/pidigits.gcc-4.gcc.c bin
	$(CC) $(CFLAGSD) -o $@ $< -lgmp
