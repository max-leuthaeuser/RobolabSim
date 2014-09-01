#include "../h/Urlcode.h"

char *concat(char* a, char* b) {
	int len = strlen(a) + strlen(b) + 1;
	char *n2a = malloc(len);

	/* conforming C99 ... */
	strncpy(n2a, a, len);
	strncat(n2a, b, len - strlen(n2a));
	return n2a;
}

bool contains(char* a, char* b) {
	return strstr(a, b) != NULL;
}

/* Converts a hex character to its integer value */
char from_hex(char ch) {
	return isdigit((unsigned char)ch) ? ch - '0' : tolower((unsigned char)ch) - 'a' + 10;
}

/* Converts an integer value to its hex character*/
char to_hex(char code) {
	static char hex[] = "0123456789abcdef";
	return hex[code & 15];
}

/* Returns a url-encoded version of str */
/* IMPORTANT: be sure to free() the returned string after use */
char *url_encode(char *str) {
	char *pstr = str, *buf = malloc(strlen(str) * 3 + 1), *pbuf = buf;
	while (*pstr) {
		if (isalnum((unsigned char)*pstr) || *pstr == '-' || *pstr == '_' || *pstr == '.'
				|| *pstr == '~')
			*pbuf++ = *pstr;
		else if (*pstr == ' ')
			*pbuf++ = '+';
		else
			*pbuf++ = '%', *pbuf++ = to_hex(*pstr >> 4), *pbuf++ = to_hex(
					*pstr & 15);
		pstr++;
	}
	*pbuf = '\0';
	return buf;
}

/* Returns a url-decoded version of str */
/* IMPORTANT: be sure to free() the returned string after use */
char *url_decode(char *str) {
	char *pstr = str, *buf = malloc(strlen(str) + 1), *pbuf = buf;
	while (*pstr) {
		if (*pstr == '%') {
			if (pstr[1] && pstr[2]) {
				*pbuf++ = from_hex(pstr[1]) << 4 | from_hex(pstr[2]);
				pstr += 2;
			}
		} else if (*pstr == '+') {
			*pbuf++ = ' ';
		} else {
			*pbuf++ = *pstr;
		}
		pstr++;
	}
	*pbuf = '\0';
	return buf;
}
