package sheetjson.player.activatable

import sheetjson.input.KeyListener.KeyCode
import sheetjson.player.activatable.IncrementalInteractivePlayer.IncrementalInteractiveSpec
import sheetjson.player.activatable.InteractivePlayer.InteractiveSpec

trait IncrementalInteractivePlayer extends InteractivePlayer[IncrementalInteractiveSpec] {
  override def keyPressed(kc: KeyCode): Unit = {
    if (kc == interactiveSpec.next)
      next()
    else
      previous()
  }

  override def keyReleased(kc: KeyCode): Unit = {}

  def next()

  def previous()
}

object IncrementalInteractivePlayer {
  case class IncrementalInteractiveSpec(previous: KeyCode, next: KeyCode)
    extends InteractiveSpec { override def keys = Seq(previous, next) }
}
