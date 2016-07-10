package sheetjson.player.filter

import java.util.concurrent.atomic.AtomicBoolean

import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.{ListenerPlayer, Playable, Player, PlayerSpec}

class Toggle( val key: KeyCode,
              _child: Player,
              _spec: PlayerSpec) extends FilterPlayer(_child, _spec) with ListenerPlayer {

  private val pressed = new AtomicBoolean(false)

  override val keys: Seq[KeyCode] = Seq(key)

  override protected def _play: Playable = {
    val played = child.play

    if (pressed.get())
      played
    else
      Playable.default
  }

  override def keyReleased(kc: KeyCode): Unit = pressed.set(!pressed.get())
}
