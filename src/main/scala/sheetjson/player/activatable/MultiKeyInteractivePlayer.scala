package sheetjson.player.activatable
import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.activatable.InteractivePlayer.InteractiveSpec
import sheetjson.player.activatable.MultiKeyInteractivePlayer.MultiKeyInteractiveSpec

trait MultiKeyInteractivePlayer extends InteractivePlayer[MultiKeyInteractiveSpec] {
  override def keyPressed(kc: KeyCode): Unit = {
    activate(keys.indexOf(kc))
  }

  override def keyReleased(kc: KeyCode): Unit = {
    deactivate(keys.indexOf(kc))
  }

  def activate(i: Int)

  def deactivate(i: Int)
}

object MultiKeyInteractivePlayer {
  case class MultiKeyInteractiveSpec(override val keys: Seq[Int])
    extends InteractiveSpec
}
