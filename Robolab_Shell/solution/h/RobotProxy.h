#ifndef ROBOTPROXY_H_
#define ROBOTPROXY_H_

#include "Configuration.h"

#define NORTH 0x10
#define SOUTH 0x20
#define WEST 0x40
#define EAST 0x80

#define D_N     0x10    // North
#define D_S     0x20    // South
#define D_NS    0x30    // North and South
#define D_W     0x40    // West
#define D_NW    0x50    // North and West
#define D_SW    0x60    // South and West
#define D_NSW   0x70    // North, South and West
#define D_E     0x80    // East
#define D_NE    0x90    // North and East
#define D_SE    0xA0    // South and East
#define D_NSE   0xB0    // North, South and East
#define D_WE    0xC0    // West and East
#define D_NWE   0xD0    // North, West and East
#define D_SWE   0xE0    // South, West and East
#define D_NSWE  0xF0    // North, South, West and East
#define ROBOT_FAIL        0x00
#define ROBOT_SUCCESS     0x01
#define ROBOT_TOKENFOUND  0x02

/// Set the robot to the specified position
/// @ returns ROBOT_SUCCESS, ROBOT_FAIL or ROBOT_TOKENFOUND
int Robot_Move(int x, int y);

/// Get the intersections of the current node that the robot is at
int Robot_GetIntersections();

#endif /* ROBOTPROXY_H_ */

