package music2.player.composite

import music2.management.KeyListener.KeyCode
import music2.player.{ListenerPlayer, Playable, Player, PlayerSpec}

class Switcher(_wrapped: Seq[(KeyCode, Player)],
               _spec: PlayerSpec) extends CompositePlayer[(KeyCode, Player)] with ListenerPlayer {
  override protected val wrapped: Seq[(KeyCode, Player)] = _wrapped

  private var current: Option[KeyCode] = None

  override protected def extract(t: (KeyCode, Player)): Player = t._2

  override protected def _play: Playable = {
    (for {
      c <- current
      player <- wrapped.toMap get c
    } yield player.play).getOrElse(Playable.default)
  }

  override val keys: Seq[KeyCode] = wrapped.map(_._1)

  override def keyReleased(kc: KeyCode): Unit = current = Some(kc)
}
