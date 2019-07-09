#
# Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
.PHONY: all

all: bin/${LIBNFI}

OBJECTS		=	main.o

OBJECTFILES	=	${OBJECTS:%.o=bin/%.o}
CFLAGS		+=	-I$(VPATH)/include
CFLAGS		+=	-O3 -g0 -fPIC
LDFLAGS		=	-g0 -s

bin/$(LIBNFI): $(OBJECTFILES)
	@echo "[LD] $@"
	@$(CC) -std=c99 $(LDFLAGS) $+ -o $@ -ldl

bin/%.d: src/%.c
	@mkdir -p $(@D)
	@$(CC) -MM $(CFLAGS) $< | sed 's|\($*\)\.o[ :]*|bin/\1.o $@ : |g' > $@

bin/%.o: src/%.c
	@echo "[CC] $@"
	@$(CC) -c $(CFLAGS) -o $@ $<

-include ${OBJECTS:%.o=bin/%.d}
