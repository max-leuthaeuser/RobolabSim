#ifndef CONFIGURATION_H_
#define CONFIGURATION_H_

#define	DEBUG

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

// user headers
#include "Communication.h"
#include "Urlcode.h"
#include "RobotProxy.h"

// define server URL here
#define URL "http://localhost:8080/query?id=someGroupID&values="

// define maze size here
#define MAZE_WIDTH 6
#define MAZE_HIGHT 6

#endif /* CONFIGURATION_H_ */
