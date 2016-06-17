package music2.management

import java.util.concurrent.ConcurrentHashMap

import music2.management.KeyListener._
import music2.player.{ListenerPlayer, Player}

import scala.collection.mutable.ArrayBuffer

class KeyListener(_rootPlayer: Player) {

  {
    // Get all players and register them if they're a listener
    Player.flatten(_rootPlayer)
      .collect { case p: ListenerPlayer =>
        listen(p, p.keys)
      }
  }

  /**
    * Map of key codes to what `Player`s are listening to that key
    */
  val listeners = new ConcurrentHashMap[KeyCode, ArrayBuffer[ListenerPlayer]]()

  /**
    * Register a listener to listen on a key
    * @param lp the listener
    * @param kc the key to listen to
    */
  def listen(lp: ListenerPlayer, kc: KeyCode): Unit =
    if (listeners contains kc)
      (listeners get kc) += lp
    else
      listeners put (kc, ArrayBuffer(lp))

  /**
    * Register a listener for multiple keys
    * @param lp the listener
    * @param kcs the keys
    */
  def listen(lp: ListenerPlayer, kcs: Traversable[KeyCode]): Unit =
    kcs foreach { listen(lp, _) }
}

object KeyListener {
  /**
    * The type of key code
    */
  type KeyCode = Int
}
