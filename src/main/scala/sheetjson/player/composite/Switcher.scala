package sheetjson.player.composite

import sheetjson.input.KeyListener.KeyCode
import sheetjson.player.listener.{Listener, ListenerPlayer, MultiActivatableListener}
import sheetjson.player.{Playable, Player, PlayerSpec}

class Switcher(_wrapped: Seq[Player],
               _spec: PlayerSpec)
    extends CompositePlayer[Player](_spec) with ListenerPlayer with MultiActivatableListener {

  override protected val wrapped: Seq[Player] = _wrapped

  override protected def extract(t: Player): Player = t

  private var currentIndex: Option[Int] = None

  override protected def _play: Playable = {
    (for {
      c <- currentIndex
      if wrapped.size > c
      player = wrapped(c)
    } yield player.play).getOrElse(Playable.default)
  }

  override val listeners: Seq[Listener] = Seq(this)

  override def _activate(i: KeyCode): Unit = {}

  override def _deactivate(i: KeyCode): Unit = currentIndex = Some(i)

  override val size: KeyCode = wrapped.size
}
