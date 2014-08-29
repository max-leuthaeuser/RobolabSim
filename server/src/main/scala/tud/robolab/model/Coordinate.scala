package tud.robolab.model

case class Coordinate(
  x: Int,
  y: Int
  )
{
  override def toString: String = "<html>x: <b>%s</b>, y: <b>%s</b></html>".format(x, y)
}