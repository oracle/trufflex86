#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
.PHONY: all

OBJECTS		=	segv.o
OBJECTS_JNI	=	mem.o

LIBXED_ILD	=	$(XED_VERSION)/lib/libxed-ild.a

OBJECTFILES	=	${OBJECTS:%.o=bin/%.o}
OBJECTFILES_JNI	=	${OBJECTS_JNI:%.o=bin/%.o}
CFLAGS		+=	-I$(VPATH)/include -Isrc_gen "-I$(JAVA_HOME)/include" "-I$(JAVA_HOME)/include/$(OS)" -I$(XED_VERSION)/include
CFLAGS		+=	-O3 -g -fPIC
LDFLAGS		=	-g -L$(XED_VERSION)/lib

ifeq ($(ARCH),amd64)
LIBFILES	=	$(LIBXED_ILD)
else
LIBFILES	=
endif

LIBMEMORY_A	=	libmemory.a
LIBMEMORY_O	=	libmemory.o

all: bin/$(LIBMEMORY) bin/$(LIBMEMORY_A)

$(LIBXED_ILD): $(XED)
	@echo "[UNTAR]     $(XED)"
	@tar xf $(XED)

bin/$(LIBMEMORY): $(LIBMEMORY_O) $(OBJECTFILES_JNI)
	@echo "[LD]        $@"
	@$(CC) -std=c99 -shared $(LDFLAGS) $+ -o $@

$(LIBMEMORY_O): $(OBJECTFILES) $(LIBFILES)
	@echo "[LD]        $@"
	@$(LD) -r -o $(LIBMEMORY_O) $^

bin/$(LIBMEMORY_A): $(LIBMEMORY_O)
	@echo "[AR]        $@"
	@$(AR) rcs $@ $(LIBMEMORY_O)

bin/%.d: src/%.c $(LIBFILES)
	@mkdir -p $(@D)
	@$(CC) -MM $(CFLAGS) $< | sed 's|\($*\)\.o[ :]*|bin/\1.o $@ : |g' > $@

bin/%.o: src/%.c $(LIBFILES)
	@echo "[CC]        $@"
	@$(CC) -c $(CFLAGS) -o $@ $<

-include ${OBJECTS:%.o=bin/%.d} ${OBJECTS_JNI:%.o=bin/%.d}
