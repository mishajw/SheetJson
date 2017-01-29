package sheetjson.player.filter

import java.util.concurrent.atomic.AtomicBoolean

import sheetjson.player.listener.{ActivatableListener, Listener, ListenerPlayer}
import sheetjson.player.{Playable, Player, PlayerSpec}

class Toggle(_child: Player,
             _spec: PlayerSpec)
    extends FilterPlayer(_child, _spec) with ListenerPlayer with ActivatableListener {

  private val pressed = new AtomicBoolean(false)

  override protected def _play: Playable = {
    val played = child.play

    if (pressed.get())
      played
    else
      Playable.default
  }

  override val listeners: Seq[Listener] = Seq(this)

  // TODO: Make a new listener for this case
  override def _activate(): Unit = {}

  override def _deactivate(): Unit = pressed.set(!pressed.get())
}
