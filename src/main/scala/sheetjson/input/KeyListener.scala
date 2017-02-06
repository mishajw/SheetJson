package sheetjson.input

import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener.KeyCode
import sheetjson.management.gui.GUI
import sheetjson.player.Player
import sheetjson.util.Messagable
import sheetjson.util.Messagable.{Message, StringMessage}

import scala.collection.mutable.ArrayBuffer

class KeyListener(var rootPlayer: Player) {

  private val log = Logger(getClass)

  /**
    * Map of key codes to what `Player`s are listening to that key
    */
  private val pressListeners = new ConcurrentHashMap[KeyCode, ArrayBuffer[Message]]()

  private val releaseListeners = new ConcurrentHashMap[KeyCode, ArrayBuffer[Message]]()

  private def sendMessage(message: Message): Unit = rootPlayer receive message

  GUI.addKeyListener(new java.awt.event.KeyListener() {
    override def keyTyped(keyEvent: KeyEvent): Unit = {}

    override def keyPressed(keyEvent: KeyEvent): Unit =
      notifyKeyPressed(keyEvent.getKeyCode)

    override def keyReleased(keyEvent: KeyEvent): Unit =
      notifyKeyReleased(keyEvent.getKeyCode)
  })

  def listenForPress(key: KeyCode, messagable: Messagable, message: String): Unit = {
    if (!(pressListeners contains key)) {
      pressListeners.put(key, ArrayBuffer())
    }

    pressListeners.get(key) += messagable.createMessage(StringMessage(message))
  }

  def listenForRelease(key: KeyCode, messagable: Messagable, message: String): Unit = {
    if (!(releaseListeners contains key)) {
      releaseListeners.put(key, ArrayBuffer())
    }

    releaseListeners.get(key) += messagable.createMessage(StringMessage(message))
  }

  def clearListeners() = {
    pressListeners.clear()
    releaseListeners.clear()
  }

  /**
    * Notify listener a key has been pressed
    *
    * @param kc the key that has been pressed
    */
  private def notifyKeyPressed(kc: KeyCode): Unit = {
    Option(pressListeners get kc)
      .foreach(ls => ls foreach sendMessage)
  }

  /**
    * Notify listener a key has been released
    *
    * @param kc the key that has been released
    */
  private def notifyKeyReleased(kc: KeyCode): Unit = {
    Option(releaseListeners get kc)
      .foreach(ls => ls foreach sendMessage)
  }
}

object KeyListener {
  /**
    * The type of key code
    */
  type KeyCode = Int
}
