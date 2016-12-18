package sheetjson.player.filter

import java.util.concurrent.atomic.AtomicBoolean

import sheetjson.player.activatable.SingleKeyInteractivePlayer
import sheetjson.player.activatable.SingleKeyInteractivePlayer.SingleKeyInteractiveSpec
import sheetjson.player.{Playable, Player, PlayerSpec}

class Toggle(_child: Player,
             _spec: PlayerSpec,
             override val interactiveSpec: SingleKeyInteractiveSpec)
    extends FilterPlayer(_child, _spec) with SingleKeyInteractivePlayer {

  private val pressed = new AtomicBoolean(false)

  override protected def _play: Playable = {
    val played = child.play

    if (pressed.get())
      played
    else
      Playable.default
  }

  override def activate(): Unit = {}

  override def deactivate(): Unit = pressed.set(!pressed.get())
}
