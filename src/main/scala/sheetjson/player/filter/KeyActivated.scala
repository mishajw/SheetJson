package sheetjson.player.filter

import java.util.concurrent.atomic.AtomicBoolean

import sheetjson.player.listener.{ActivatableListener, Listener, ListenerPlayer}
import sheetjson.player.{Playable, Player, PlayerSpec}

class KeyActivated(_child: Player,
                   _spec: PlayerSpec)
    extends FilterPlayer(_child, _spec) with ListenerPlayer with ActivatableListener {

  private val _pressed = new AtomicBoolean(false)

  def pressed = _pressed.get()

  override protected def _play: Playable = {
    val played = child.play

    if (_pressed.get()) played
    else Playable.default
  }

  override val listeners: Seq[Listener] = Seq(this)

  override def _activate(): Unit = {
    _pressed set true
  }

  override def _deactivate(): Unit = {
    _pressed set false
  }
}
