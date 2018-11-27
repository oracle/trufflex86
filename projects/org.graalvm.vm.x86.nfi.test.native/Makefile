.PHONY: all

all: bin/${LIBTEST}

OBJECTS		=	fourtytwo.o inc.o add.o null.o numeric.o string.o object.o

OBJECTFILES	=	${OBJECTS:%.o=bin/%.o}
CFLAGS		+=	-I$(VPATH)/include
CFLAGS		+=	-O3 -g -fPIC
LDFLAGS		=	-g

bin/$(LIBTEST): $(OBJECTFILES)
	@echo "[LD] $@"
	@$(CC) -std=c99 -shared $(LDFLAGS) $+ -o $@

bin/%.d: src/%.c
	@mkdir -p $(@D)
	@$(CC) -MM $(CFLAGS) $< | sed 's|\($*\)\.o[ :]*|bin/\1.o $@ : |g' > $@

bin/%.o: src/%.c
	@echo "[CC] $@"
	@$(CC) -c $(CFLAGS) -o $@ $<

-include ${OBJECTS:%.o=bin/%.d}
