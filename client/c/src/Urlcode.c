#include "Urlcode.h"

char *concat(const char* a, const char* b) {
	int len = strlen(a) + strlen(b) + 1;
	char *n2a = malloc(len);

	/* conforming C99 ... */
	strncpy(n2a, a, len);
	strncat(n2a, b, len - strlen(n2a));
	return n2a;
}

bool contains(const char* a, const char* b) {
	return strstr(a, b) != NULL;
}