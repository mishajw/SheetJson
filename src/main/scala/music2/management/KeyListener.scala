package music2.management

import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap
import javax.swing.JFrame

import music2.management.KeyListener._
import music2.player.{ListenerPlayer, Player}

import scala.collection.mutable.ArrayBuffer

class KeyListener(_rootPlayer: Player) {

  /**
    * Map of key codes to what `Player`s are listening to that key
    */
  private val listeners = new ConcurrentHashMap[KeyCode, ArrayBuffer[ListenerPlayer]]()

  {
    // Get all players and register them if they're a listener
    Player.flatten(_rootPlayer)
      .collect { case p: ListenerPlayer =>
        listen(p, p.keys)
      }
  }

  {
    // Setup a window for listening to keys
    val f = new JFrame("music2")
    f.setSize(10, 10)
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    f.setVisible(true)

    f.addKeyListener(new java.awt.event.KeyListener() {
      override def keyTyped(keyEvent: KeyEvent): Unit = {}

      override def keyPressed(keyEvent: KeyEvent): Unit =
        notifyKeyReleased(keyEvent.getKeyCode)

      override def keyReleased(keyEvent: KeyEvent): Unit =
        notifyKeyPressed(keyEvent.getKeyCode)
    })
  }

  /**
    * Register a listener to listen on a key
    * @param lp the listener
    * @param kc the key to listen to
    */
  private def listen(lp: ListenerPlayer, kc: KeyCode): Unit =
    if (listeners contains kc)
      (listeners get kc) += lp
    else
      listeners put (kc, ArrayBuffer(lp))

  /**
    * Register a listener for multiple keys
    * @param lp the listener
    * @param kcs the keys
    */
  private def listen(lp: ListenerPlayer, kcs: Traversable[KeyCode]): Unit =
    kcs foreach { listen(lp, _) }

  /**
    * Notify listener a key has been pressed
    * @param kc the key that has been pressed
    */
  private def notifyKeyPressed(kc: KeyCode): Unit = {
    Option(listeners get kc)
      .foreach(ls => ls.foreach(_.keyPressed(kc)))
  }

  /**
    * Notify listener a key has been released
    * @param kc the key that has been released
    */
  private def notifyKeyReleased(kc: KeyCode): Unit = {
    Option(listeners get kc)
      .foreach(ls => ls.foreach(_.keyReleased(kc)))
  }
}

object KeyListener {
  /**
    * The type of key code
    */
  type KeyCode = Int
}
