package sheetjson.player.composite

import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.{ListenerPlayer, Playable, Player, PlayerSpec}

class Switcher(_wrapped: Seq[(KeyCode, Player)],
               _spec: PlayerSpec) extends CompositePlayer[(KeyCode, Player)](_spec) with ListenerPlayer {
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
