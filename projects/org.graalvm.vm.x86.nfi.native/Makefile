.PHONY: all

all: bin/${LIBNFI}

OBJECTS		=	main.o

OBJECTFILES	=	${OBJECTS:%.o=bin/%.o}
CFLAGS		+=	-I$(VPATH)/include
CFLAGS		+=	-O3 -g0 -fPIC
LDFLAGS		=	-g0

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
