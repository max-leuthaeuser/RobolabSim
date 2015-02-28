# Makefile by David Ordnung

PROJECT = RobolabSimClient
OBJECTS = src/Communication.c src/RobolabSimClient.c src/RobotProxy.c src/Urlcode.c
C = gcc
C_OSX = clang

INCLUDE += -I. -Ic -Ih
CFLAGS += -DCURL_STATICLIB -Wall -Werror -msse -DHAVE_STDINT_H
LINK += -lcurl -lm -ldl

BINARY = $(PROJECT)
OS := $(shell uname -s)

ifeq "$(OS)" "Darwin"
	C = $(C_OSX)
	CFLAGS += -DOSX -D_OSX
	LINK += -mmacosx-version-min=10.5
	BIN_DIR = builds/osx
else
	ifeq "$(OS)" "Linux"
		CFLAGS += -D_LINUX
		BIN_DIR = builds/linux
	else
		CFLAGS += -D_CYGWIN
		BIN_DIR = builds/win
	endif
	
	LINK += -lrt
	CFLAGS += -DCOMPILER_GCC
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
	rm -rf $(BIN_DIR)/src
	rm -rf $(BIN_DIR)/$(BINARY)
	
cleancompile:
	rm -rf $(BIN_DIR)/src
