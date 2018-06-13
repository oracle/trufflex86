QUIET	:= @

CC	:=	gcc
CXX	:=	g++
CFLAGS	:=	-static -O3 -fdata-sections -ffunction-sections -Wl,--gc-sections \
		-I$(PWD)/tests/benchmarksgame
CXXFLAGS:=	-static -O3 -fdata-sections -ffunction-sections -Wl,--gc-sections \
		-I$(PWD)/tests/benchmarksgame
CFLAGSD	:=	-O3 -fdata-sections -ffunction-sections -Wl,--gc-sections

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
		bin/pidigits.cint-4 bin/pidigits.gcc bin/pidigits.gcc-4 \
		bin/nbody.cint bin/nbody.gcc-2 bin/nbody.gcc-3 bin/nbody.gcc-4 \
		bin/nbody.gcc-6 bin/nbody.gcc bin/nbody.gpp-2 bin/nbody.gpp-3 \
		bin/nbody.gpp-5 bin/nbody.gpp-6 bin/nbody.gpp-7 bin/nbody.gpp-8\
		bin/nbody.gpp \
		bin/meteor.cint bin/meteor.gcc bin/meteor.gpp bin/meteor.gpp-2 \
		bin/meteor.gpp-3 bin/meteor.gpp-4 bin/meteor.gpp-5 \
		bin/meteor.gpp-6 \
		bin/knucleotide.cint bin/knucleotide.gcc-8


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
			bin/pidigits.cint-4 bin/pidigits.gcc bin/pidigits.gcc-4\
			bin/nbody.cint bin/nbody.gcc-2 bin/nbody.gcc-3 \
			bin/nbody.gcc-4 bin/nbody.gcc-6 bin/nbody.gcc \
			bin/nbody.gpp-2 bin/nbody.gpp-3 bin/nbody.gpp-5 \
			bin/nbody.gpp-6 bin/nbody.gpp-7 bin/nbody.gpp-8 \
			bin/nbody.gpp \
			bin/meteor.cint bin/meteor.gcc bin/meteor.gpp \
			bin/meteor.gpp-2 bin/meteor.gpp-3 bin/meteor.gpp-4 \
			bin/meteor.gpp-5 bin/meteor.gpp-6 \
			bin/knucleotide.cint bin/knucleotide.gcc-8

bin/binarytrees.gcc: binarytrees/binarytrees.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/binarytrees.gcc-2: binarytrees/binarytrees.gcc-2.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fasta.cint: fasta/fasta.cint.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fasta.gcc: fasta/fasta.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fasta.gcc-4: fasta/fasta.gcc-4.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fastaredux.gcc-2: fastaredux/fastaredux.gcc-2.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fastaredux.gcc-3: fastaredux/fastaredux.gcc-3.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fastaredux.gcc-5: fastaredux/fastaredux.gcc-5.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fastaredux.gpp: fastaredux/fastaredux.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/fastaredux.gpp-2: fastaredux/fastaredux.gpp-2.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/fastaredux.gpp-5: fastaredux/fastaredux.gpp-5.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/fannkuchredux.cint: fannkuchredux/fannkuchredux.cint.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fannkuchredux.gcc: fannkuchredux/fannkuchredux.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/fannkuchredux.gcc-3: fannkuchredux/fannkuchredux.gcc-3.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.cint: mandelbrot/mandelbrot.cint-2.cint.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gcc-2: mandelbrot/mandelbrot.gcc-2.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gcc-4: mandelbrot/mandelbrot.gcc-4.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gcc-8: mandelbrot/mandelbrot.gcc-8.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gcc-9: mandelbrot/mandelbrot.gcc-9.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $<

bin/mandelbrot.gpp: mandelbrot/mandelbrot.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-2: mandelbrot/mandelbrot.gpp-2.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-3: mandelbrot/mandelbrot.gpp-3.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-5: mandelbrot/mandelbrot.gpp-5.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-6: mandelbrot/mandelbrot.gpp-6.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-7: mandelbrot/mandelbrot.gpp-7.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-8: mandelbrot/mandelbrot.gpp-8.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/mandelbrot.gpp-9: mandelbrot/mandelbrot.gpp-9.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $<

bin/spectralnorm.cint: spectralnorm/spectralnorm.cint.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/spectralnorm.gcc: spectralnorm/spectralnorm.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/spectralnorm.gcc-2: spectralnorm/spectralnorm.gcc-2.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/spectralnorm.gcc-3: spectralnorm/spectralnorm.gcc-3.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/spectralnorm.gcc-5: spectralnorm/spectralnorm.gcc-5.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/pidigits.cint-4: pidigits/pidigits.cint-4.cint.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGSD) -o $@ $< -lgmp

bin/pidigits.gcc: pidigits/pidigits.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGSD) -o $@ $< -lgmp

bin/pidigits.gcc-4: pidigits/pidigits.gcc-4.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGSD) -o $@ $< -lgmp

bin/nbody.cint: nbody/nbody.cint.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/nbody.gcc: nbody/nbody.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/nbody.gcc-2: nbody/nbody.gcc-2.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/nbody.gcc-3: nbody/nbody.gcc-3.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/nbody.gcc-4: nbody/nbody.gcc-4.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/nbody.gcc-6: nbody/nbody.gcc-6.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/nbody.gpp: nbody/nbody.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/nbody.gpp-2: nbody/nbody.gpp-2.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/nbody.gpp-3: nbody/nbody.gpp-3.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/nbody.gpp-5: nbody/nbody.gpp-5.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/nbody.gpp-6: nbody/nbody.gpp-6.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/nbody.gpp-7: nbody/nbody.gpp-7.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/nbody.gpp-8: nbody/nbody.gpp-8.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/meteor.cint: meteor/meteor.cint.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/meteor.gcc: meteor/meteor.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/meteor.gpp: meteor/meteor.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/meteor.gpp-2: meteor/meteor.gpp-2.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/meteor.gpp-3: meteor/meteor.gpp-3.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/meteor.gpp-4: meteor/meteor.gpp-4.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/meteor.gpp-5: meteor/meteor.gpp-5.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/meteor.gpp-6: meteor/meteor.gpp-6.gpp.cpp
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CXXLD]     $@"
	$(QUIET)$(CXX) $(CXXFLAGS) -o $@ $< -lm

bin/knucleotide.cint: knucleotide/knucleotide.cint.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm

bin/knucleotide.gcc-8: knucleotide/knucleotide.gcc-8.gcc.c
	$(QUIET)[ ! -d bin ] && mkdir bin || true
	$(QUIET)echo "[CCLD]      $@"
	$(QUIET)$(CC) $(CFLAGS) -o $@ $< -lm
