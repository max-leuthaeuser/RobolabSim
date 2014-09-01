package tud.robolab.model

/**
 * A case class representing a Coordinate in the [[tud.robolab.model.Maze]] as tuple of two Ints.
 *
 * @param x the x coordinate as Int
 * @param y the y coordinate as Int
 */
case class Coordinate(
  x: Int,
  y: Int
  )
{
  override def toString: String = "<html>x: <b>%s</b>, y: <b>%s</b></html>".format(x, y)
}