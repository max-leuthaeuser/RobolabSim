package tud.robolab.model

sealed abstract class Message

case class Request(id: String, x: Int, y: Int) extends Message

case class Response(id: String, x: Int, y: Int) extends Message