package music2.player

import music2.management.KeyListener.KeyCode

trait ListenerPlayer {
  def keyPressed(kc: KeyCode): Unit = {}
  def keyReleased(kc: KeyCode): Unit = {}
}
