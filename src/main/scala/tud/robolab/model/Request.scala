package tud.robolab.model

import Direction._

case class Request(val id: String, val dir: Direction, val map: Maze)
