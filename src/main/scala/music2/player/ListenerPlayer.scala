package music2.player

import music2.management.KeyListener.KeyCode

trait ListenerPlayer {

  /**
    * The keys to listen to
    */
  val keys: Seq[KeyCode]

  /**
    * Called when a key is pressed
    * @param kc the key code of the pressed key
    */
  def keyPressed(kc: KeyCode): Unit = {}

  /**
    * Called when a key is released
    * @param kc the key code of the released key
    */
  def keyReleased(kc: KeyCode): Unit = {}
}
