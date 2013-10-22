#include "../h/Configuration.h"

int main(void) {

	printf("Token: %d\n", Robot_Move(0, 0));
	printf("Intersection: %d\n", Robot_GetIntersections());

	return EXIT_SUCCESS;
}