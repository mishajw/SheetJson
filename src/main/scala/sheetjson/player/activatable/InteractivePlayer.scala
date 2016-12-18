package sheetjson.player.activatable

import sheetjson.management.KeyListener._
import sheetjson.player.activatable.InteractivePlayer.InteractiveSpec

trait InteractivePlayer[SpecType <: InteractiveSpec] {

  val interactiveSpec: SpecType

  val keys = interactiveSpec.keys

  /**
    * Called when a key is pressed
    *
    * @param kc the key code of the pressed key
    */
  def keyPressed(kc: KeyCode): Unit

  /**
    * Called when a key is released
    *
    * @param kc the key code of the released key
    */
  def keyReleased(kc: KeyCode): Unit
}

object InteractivePlayer {
  trait InteractiveSpec {
    def keys: Seq[KeyCode]
  }
}
