package tud.robolab.model

object Direction extends Enumeration {
  type Direction = Value
  val NORTH, EAST, SOUTH, WEST = Value

  def from(dir: String): Direction = dir match {
    case "NORTH" => NORTH
    case "EAST" => EAST
    case "SOUTH" => SOUTH
    case "WEST" => WEST
    case c => throw new IllegalArgumentException("" + c)
  }
}
