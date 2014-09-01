#include "../h/Configuration.h"
#include "../curl/curl/curl.h"

int main(void) {
	// do *NOT* remove the following line, it is necessary
	// for the communication with the simulation server
	curl_global_init(CURL_GLOBAL_ALL);

	printf("Token: %x\n", Robot_Move(0, 0));
	printf("Intersection: %x\n", Robot_GetIntersections());
	printf("Token: %x\n", Robot_Move(1, 0));
	printf("Intersection: %x\n", Robot_GetIntersections());
	printf("Token: %x\n", Robot_Move(2, 0));

	// do *NOT* remove the following line, it is necessary
	// for the communication with the simulation server
	curl_global_cleanup();

	return EXIT_SUCCESS;
}