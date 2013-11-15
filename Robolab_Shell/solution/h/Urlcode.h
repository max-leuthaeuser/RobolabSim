#ifndef URLCODE_H_
#define URLCODE_H_

#include "Configuration.h"

char *url_encode(char *str);
char *url_decode(char *str);
char *concat(char* a, char* b);
bool contains(char* a, char* b);

#endif /* URLCODE_H_ */
