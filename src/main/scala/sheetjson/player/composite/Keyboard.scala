package sheetjson.player.composite

import sheetjson.player._
import sheetjson.player.listener.{ActivatableListener, Listener, ListenerPlayer, MultiActivatableListener}

class Keyboard(components: Seq[ActivatableListener],
               _spec: PlayerSpec)
    extends CompositePlayer[Player](_spec) with ListenerPlayer with MultiActivatableListener {

  override protected val wrapped: Seq[Player] =
    components collect { case p: Player => p }

  override protected def extract(t: Player): Player = t

  override protected def _play: Playable = {
    wrapped.map(_.play).combine
  }

  override val listeners: Seq[Listener] = Seq(this)

  override def _activate(i: Int): Unit = components(i).activate()

  override def _deactivate(i: Int): Unit = components(i).deactivate()

  override val size: Int = components.size
}
