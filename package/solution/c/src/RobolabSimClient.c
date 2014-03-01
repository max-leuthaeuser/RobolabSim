#include "../h/Configuration.h"
#include "../curl/curl/curl.h"

int main(void) {
	curl_global_init(CURL_GLOBAL_ALL);

	printf("Token: %d\n", Robot_Move(0, 0));
	printf("Intersection: %d\n", Robot_GetIntersections());
	printf("Token: %d\n", Robot_Move(1, 0));
	printf("Intersection: %d\n", Robot_GetIntersections());
	printf("Token: %d\n", Robot_Move(2, 0));

	curl_global_cleanup();

	return EXIT_SUCCESS;
}