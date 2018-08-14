.PHONY: all

all: bin/${LIBMEMORY}

OBJECTS		=	mem.o

LIBXED_ILD	=	$(XED_VERSION)/lib/libxed-ild.a

OBJECTFILES	=	${OBJECTS:%.o=bin/%.o}
CFLAGS		+=	-I$(VPATH)/include -Isrc_gen "-I$(JAVA_HOME)/include" "-I$(JAVA_HOME)/include/$(OS)" -I$(XED_VERSION)/include
CFLAGS		+=	-O3 -g -fPIC
LDFLAGS		=	-g -L$(XED_VERSION)/lib

ifeq ($(ARCH),amd64)
LIBS		=	-lxed-ild
LIBFILES	=	$(LIBXED_ILD)
else
LIBS		=
LIBFILES	=
endif

$(LIBXED_ILD): $(XED)
	@echo "[UNTAR]     $(XED)"
	@tar xf $(XED)

bin/$(LIBMEMORY): $(OBJECTFILES) $(LIBFILES)
	@echo "[LD]        $@"
	@$(CC) -std=c99 -shared $(LDFLAGS) $+ -o $@ $(LIBS)

bin/%.d: src/%.c $(LIBFILES)
	@mkdir -p $(@D)
	@$(CC) -MM $(CFLAGS) $< | sed 's|\($*\)\.o[ :]*|bin/\1.o $@ : |g' > $@

bin/%.o: src/%.c $(LIBFILES)
	@echo "[CC]        $@"
	@$(CC) -c $(CFLAGS) -o $@ $<

-include ${OBJECTS:%.o=bin/%.d}
