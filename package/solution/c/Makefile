PROJECT = RobolabSimClient
OBJECTS = src/Communication.c src/RobolabSimClient.c src/RobotProxy.c src/Urlcode.c
C = gcc
C_OSX = clang

INCLUDE += -I. -Ic -Ih -Icurl/curl
LINK += -m32 -lm -ldl -lrt

CFLAGS += -std=c99 -DPOSIX -DCURL_STATICLIB -DCOMPILER_GCC -Wall -Werror \
	-msse -DHAVE_STDINT_H -m32

BINARY = $(PROJECT)
BIN_DIR = Release

OS := $(shell uname -s)

ifeq "$(OS)" "Darwin"
	C = $(C_OSX)
	CFLAGS += -DOSX -D_OSX
	LINK += -mmacosx-version-min=10.5 curl/libs/darwin/libcurl.a
endif

ifeq "$(OS)" "Linux"
	CFLAGS += -D_LINUX
	LINK += curl/libs/linux/libcurl.a
else
	CFLAGS += -D_CYGWIN
	LINK += curl/libs/cygwin/libcurl.a
endif

OBJ_BIN := $(OBJECTS:%.c=$(BIN_DIR)/%.o)

$(BIN_DIR)/%.o: %.c
	$(C) $(INCLUDE) $(CFLAGS) -o $@ -c $<
	
all: check $(OBJ_BIN)
	$(C) $(INCLUDE) $(OBJ_BIN) $(LINK) -o $(BIN_DIR)/$(BINARY)

check:
	mkdir -p $(BIN_DIR)/src
	
default: all
	
clean:
	rm -rf $(BIN_DIR)/src/*.o
	rm -rf $(BIN_DIR)/$(BINARY)

