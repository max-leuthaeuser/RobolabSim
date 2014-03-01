#include "../h/Communication.h"
#include "../curl/curl/curl.h"

typedef struct {
	char *pResultString;
	size_t size;

} CurlReturn;


size_t PageReceive(void *buffer, size_t size, size_t nmemb, void *stream);


char* sendAndRecieve(const char* url) {
	CURL *curl = curl_easy_init();

	if (curl != NULL) {
		CurlReturn pReturn;

		pReturn.pResultString = malloc(1);
		pReturn.size = 0;

		curl_easy_setopt(curl, CURLOPT_URL, url);
		curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 0);
		curl_easy_setopt(curl, CURLOPT_CONNECTTIMEOUT, 5);
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, PageReceive);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &pReturn);
		curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0);

		if (curl_easy_perform(curl) == CURLE_OK) {
			curl_easy_cleanup(curl);

			return pReturn.pResultString;
		}

		else if (pReturn.pResultString != NULL) {
			free(pReturn.pResultString);
		}
	}

	curl_easy_cleanup(curl);

	return NULL;
}


size_t PageReceive(void *buffer, size_t size, size_t nmemb, void *stream) {
	size_t realsize = size * nmemb;
	CurlReturn *pReturn = (CurlReturn *)stream;

	pReturn->pResultString = (char *)realloc(pReturn->pResultString, pReturn->size + realsize + 1);

	if (pReturn->pResultString == NULL) {
		return 0;
	}

	memcpy(&(pReturn->pResultString[pReturn->size]), buffer, realsize);
	pReturn->size += realsize;
	pReturn->pResultString[pReturn->size] = 0;

	return realsize;
}