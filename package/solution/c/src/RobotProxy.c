#include <stdarg.h>
#include "../h/RobotProxy.h"

/// initialized with ROBOT_FAIL
int currentIntersection = ROBOT_FAIL;

#if !defined(vasprintf)
int vasprintf(char **s, const char *format, va_list ap) {
	/* Guess we need no more than 100 bytes. */
	int n, size = 100;
	va_list save_ap;

	if ((*s = (char*) malloc(size)) == NULL)
		return -1;
	while (1) {
		/* work on a copy of the va_list because of a bug
		 in the vsnprintf implementation in x86_64 libc
		 */
#ifdef __va_copy
		__va_copy(save_ap, ap);
#else
		save_ap = ap;
#endif
		/* Try to print in the allocated space. */
#ifdef _vsnprintf
		n = _vsnprintf(*s, size, format, save_ap);
#else
		n = vsnprintf(*s, size, format, save_ap);
#endif
		va_end(save_ap);
		/* If that worked, return the string. */
		if (n > -1 && n < size) {
			return n;
		}
		/* Else try again with more space. */
		if (n > -1) { /* glibc 2.1 */
			size = n + 1; /* precisely what is needed */
		} else { /* glibc 2.0 */
			size *= 2; /* twice the old size */
		}
		if ((*s = (char*) realloc(*s, size)) == NULL) {
			return -1;
		}
	}
}
#endif

#if !defined(asprintf)
int asprintf(char **s, const char *format, ...) {
	va_list vals;
	int result;

	va_start(vals, format);
	result = vasprintf(s, format, vals);
	va_end(vals);
	return result;
}
#endif

/// Set the robot to the specified position
/// @ returns ROBOT_SUCCESS, ROBOT_FAIL or ROBOT_TOKENFOUND
int Robot_Move(int x, int y) {
	char* buffer;
	asprintf(&buffer, "{\"x\":%d,\"y\":%d}", x, y);
	char* query = url_encode(buffer);
	free(buffer);
	char* response = sendAndRecieve(concat(URL, query));

	if (response == NULL) {
		puts("Connection to server failed!");
		return ROBOT_FAIL;
	}

	if (contains(response, "\"code\":1")) {
		puts("Connection declined!");
		return ROBOT_FAIL;
	}

	if (contains(response, "\"code\":2")) {
		puts("Connection blocked!");
		return ROBOT_FAIL;
	}
	
	if (contains(response, "\"code\":3")) {
		printf("Invalid position! (x=%d, y=%d)\n", x, y);
		return ROBOT_FAIL;
	}

	int foundIntersection = 0;
	bool token = false;

	if (contains(response, "\"north\":true"))
		foundIntersection |= D_N;
	if (contains(response, "\"east\":true"))
		foundIntersection |= D_E;
	if (contains(response, "\"south\":true"))
		foundIntersection |= D_S;
	if (contains(response, "\"west\":true"))
		foundIntersection |= D_W;

	if (contains(response, "\"token\":true"))
		token = true;

	free(query);

	currentIntersection = foundIntersection;
	if (token)
		return ROBOT_TOKENFOUND;

	return ROBOT_SUCCESS;
}

/// Get the intersections of the current node that the robot is at
/// @ returns always the intersection at position x=0,y=0 if Robot_Move was not called first
int Robot_GetIntersections() {
	if (currentIntersection == ROBOT_FAIL)
		Robot_Move(0, 0);
	return currentIntersection;
}
