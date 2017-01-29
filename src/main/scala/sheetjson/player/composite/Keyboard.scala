package sheetjson.player.composite

import sheetjson.input.KeyListener.KeyCode
import sheetjson.player._
import sheetjson.player.listener.{Listener, ListenerPlayer, MultiActivatableListener}

class Keyboard(components: Seq[ListenerPlayer],
               _spec: PlayerSpec)
    extends CompositePlayer[Player](_spec) with ListenerPlayer {

  override protected val wrapped: Seq[Player] =
    components collect { case p: Player => p }

  override protected def extract(t: Player): Player = t

  override protected def _play: Playable = {
    wrapped.map(_.play).combine
  }

  override val listeners: Seq[Listener] = Seq(new MultiActivatableListener {
    override def _activate(i: KeyCode): Unit =
      components(i).listeners.foreach(_.receive("activate"))

    override def _deactivate(i: KeyCode): Unit =
      components(i).listeners.foreach(_.receive("deactivate"))

    override val size: KeyCode = components.size
  })
}
