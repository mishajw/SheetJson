package music2.player.composite

import music2.management.KeyListener.KeyCode
import music2.player.filter.KeyActivated
import music2.player.{Playable, Player, PlayerSpec}

class Keyboard(playerKeys: Seq[(Player, KeyCode)],
               _spec: PlayerSpec) extends CompositePlayer[Player](_spec) {

  override protected val wrapped: Seq[Player] = {
    playerKeys map { case (p, k) => new KeyActivated(k, p, PlayerSpec()) }
  }

  override protected def extract(t: Player): Player = t

  override protected def _play: Playable = {
    val (pr, npr) = components
      .map(_.asInstanceOf[KeyActivated])
      .partition(_.pressed)

    npr.foreach(_.play)

    pr.map(_.play) combine
  }
}
