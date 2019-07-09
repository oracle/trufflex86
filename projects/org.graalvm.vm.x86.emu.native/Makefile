#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
.PHONY: all

all: bin/${LIBEMU86}

OBJECTS		=	emu.o

OBJECTFILES	=	${OBJECTS:%.o=bin/%.o}
CFLAGS		+=	-I$(VPATH)/include -Isrc_gen "-I$(JAVA_HOME)/include" "-I$(JAVA_HOME)/include/$(OS)"
CFLAGS		+=	-O3 -g -fPIC
LDFLAGS		=	-g

bin/$(LIBEMU86): $(OBJECTFILES)
	@echo "[LD]        $@"
	@$(CC) -std=c99 -shared $(LDFLAGS) $+ -o $@

bin/%.d: src/%.c
	@mkdir -p $(@D)
	@$(CC) -MM $(CFLAGS) $< | sed 's|\($*\)\.o[ :]*|bin/\1.o $@ : |g' > $@

bin/%.o: src/%.c
	@echo "[CC]        $@"
	@$(CC) -c $(CFLAGS) -o $@ $<

-include ${OBJECTS:%.o=bin/%.d}
