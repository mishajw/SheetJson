package sheetjson.input

import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener.KeyCode
import sheetjson.management.Composer.MessageSender
import sheetjson.management.Identifiable
import sheetjson.management.gui.GUI

import scala.collection.mutable.ArrayBuffer

class KeyListener {

  private val log = Logger(getClass)

  /**
    * Map of key codes to what `Player`s are listening to that key
    */
  private val pressListeners = new ConcurrentHashMap[KeyCode, ArrayBuffer[(String, String)]]()

  private val releaseListeners = new ConcurrentHashMap[KeyCode, ArrayBuffer[(String, String)]]()

  private var messageSenderOpt: Option[MessageSender] = None

  private def sendMessage(identifier: String, message: String): Unit = messageSenderOpt match {
    case Some(messageSender) => messageSender(identifier, message)
    case _ =>
  }

  GUI.addKeyListener(new java.awt.event.KeyListener() {
    override def keyTyped(keyEvent: KeyEvent): Unit = {}

    override def keyPressed(keyEvent: KeyEvent): Unit =
      notifyKeyPressed(keyEvent.getKeyCode)

    override def keyReleased(keyEvent: KeyEvent): Unit =
      notifyKeyReleased(keyEvent.getKeyCode)
  })

  def listenForPress(key: KeyCode, identifiable: Identifiable, message: String): Unit = {
    if (!(pressListeners contains key)) {
      pressListeners.put(key, ArrayBuffer())
    }

    pressListeners.get(key) += ((identifiable.identifier, message))
  }

  def listenForRelease(key: KeyCode, identifiable: Identifiable, message: String): Unit = {
    if (!(releaseListeners contains key)) {
      releaseListeners.put(key, ArrayBuffer())
    }

    releaseListeners.get(key) += ((identifiable.identifier, message))
  }

  /**
    * Notify listener a key has been pressed
    *
    * @param kc the key that has been pressed
    */
  private def notifyKeyPressed(kc: KeyCode): Unit = {
    Option(pressListeners get kc)
      .foreach(ls => ls foreach { case (identifier, message) =>
        sendMessage(identifier, message)
      })
  }

  /**
    * Notify listener a key has been released
    *
    * @param kc the key that has been released
    */
  private def notifyKeyReleased(kc: KeyCode): Unit = {
    Option(releaseListeners get kc)
      .foreach(ls => ls foreach { case (identifier, message) =>
        sendMessage(identifier, message)
      })
  }

  def registerMessageSender(messageSender: MessageSender): Unit = {
    messageSenderOpt = Some(messageSender)
  }
}

object KeyListener {
  /**
    * The type of key code
    */
  type KeyCode = Int
}
