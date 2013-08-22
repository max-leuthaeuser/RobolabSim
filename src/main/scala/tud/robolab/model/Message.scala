package tud.robolab.model

sealed abstract class Message

case class Request(x: Int, y: Int) extends Message

case class Response(north: Boolean, east: Boolean, south: Boolean, west: Boolean) extends Message

case class ErrorMessage(message: String) extends Message