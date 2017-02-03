package sheetjson.util

import com.typesafe.scalalogging.Logger
import sheetjson.util.Messagable.{ChildMessage, Message, StringMessage}

trait Messagable extends Identifiable {

  private val log = Logger(getClass)

  def receive(message: Message): Unit = message match {
    case ChildMessage(name, m) =>
      identifiableChildren
        .collect { case c: Messagable => c }
        .filter(_.toString == name)
        .foreach(_.receive(m))
    case _ =>
      log.warn(s"Received unparsed message: $message")
  }

  def createMessage(message: Message): Message = parentOpt match {
    case Some(parent: Messagable) =>
      parent.createMessage(ChildMessage(toString, message))
    case _ =>
      message
  }
}

object Messagable {
  sealed trait Message
  case class StringMessage(message: String) extends Message
  case class ChildMessage(name: String, message: Message) extends Message
}
