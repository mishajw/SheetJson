package sheetjson.input

import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.Logger
import sheetjson.util.Messagable.{Message, StringMessage}
import sheetjson.util.{Messagable, RootPlayerAssignable}

import scala.collection.mutable.ArrayBuffer

class KeyListener extends RootPlayerAssignable with java.awt.event.KeyListener {

  private val log = Logger(getClass)

  /**
    * Map of key codes to what `Player`s are listening to that key
    */
  private val pressListeners = new ConcurrentHashMap[Int, ArrayBuffer[Message]]()

  private val releaseListeners = new ConcurrentHashMap[Int, ArrayBuffer[Message]]()

  private def sendMessage(message: Message): Unit = rootPlayerOpt foreach (_.receive(message))

  override def keyTyped(keyEvent: KeyEvent): Unit = {}

  override def keyPressed(keyEvent: KeyEvent): Unit =
    notifyKeyPressed(keyEvent.getKeyCode)

  override def keyReleased(keyEvent: KeyEvent): Unit =
    notifyKeyReleased(keyEvent.getKeyCode)

  def listenForPress(key: Int, messagable: Messagable, message: String): Unit = {
    if (!(pressListeners containsKey key)) {
      pressListeners.put(key, ArrayBuffer())
    }

    pressListeners.get(key) += messagable.createMessage(StringMessage(message))
  }

  def listenForRelease(key: Int, messagable: Messagable, message: String): Unit = {
    if (!(releaseListeners containsKey key)) {
      releaseListeners.put(key, ArrayBuffer())
    }

    releaseListeners.get(key) += messagable.createMessage(StringMessage(message))
  }

  override protected def newPlayerAssigned(): Unit = {
    pressListeners.clear()
    releaseListeners.clear()
  }

  /**
    * Notify listener a key has been pressed
    *
    * @param kc the key that has been pressed
    */
  private def notifyKeyPressed(kc: Int): Unit = {
    Option(pressListeners get kc)
      .foreach(ls => ls foreach sendMessage)
  }

  /**
    * Notify listener a key has been released
    *
    * @param kc the key that has been released
    */
  private def notifyKeyReleased(kc: Int): Unit = {
    Option(releaseListeners get kc)
      .foreach(ls => ls foreach sendMessage)
  }
}
