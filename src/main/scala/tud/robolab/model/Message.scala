package tud.robolab.model

sealed abstract class Message

case class Request(x: Int, y: Int) extends Message

case class Response(north: Boolean, east: Boolean, south: Boolean, west: Boolean) extends Message

case class ErrorMessage(code: Int, message: String) extends Message

object ErrorType {
  val DENIED = ErrorMessage(1, "Connection denied!")
  val BLOCKED = ErrorMessage(2, "Connection blocked!")
  val INVALID = ErrorMessage(3, "Invalid position!")
}