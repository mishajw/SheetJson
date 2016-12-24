package sheetjson.player.filter

import java.util.concurrent.atomic.AtomicBoolean

import sheetjson.player.activatable.SingleKeyInteractivePlayer
import sheetjson.player.activatable.SingleKeyInteractivePlayer.SingleKeyInteractiveSpec
import sheetjson.player.{Playable, Player, PlayerSpec}

class KeyActivated(_child: Player,
                   _spec: PlayerSpec,
                   override val interactiveSpec: SingleKeyInteractiveSpec)
    extends FilterPlayer(_child, _spec) with SingleKeyInteractivePlayer {

  private val _pressed = new AtomicBoolean(false)

  def pressed = _pressed.get()

  override protected def _play: Playable = {
    val played = child.play

    if (_pressed.get()) played
    else Playable.default
  }

  override def _activate(): Unit = {
    _pressed set true
  }

  override def _deactivate(): Unit = {
    _pressed set false
  }
}
