#include "../h/Configuration.h"

int main(void) {
	char* url = "http://localhost:8080/query?=";
	char* query = url_encode("{\"x\":0,\"y\":0}");
	char* r = sendAndRecieve(concat(url, query));
	free(query);
	if (r != NULL)
		printf("Received:\n%s", r);
	else
		puts("Connection failed!");
	free(r);
	return EXIT_SUCCESS;
}
