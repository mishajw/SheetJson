package sheetjson.player.composite

import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.filter.KeyActivated
import sheetjson.player.{Playable, Player, PlayerSpec}

class Keyboard(playerKeys: Seq[(Player, KeyCode)],
               _spec: PlayerSpec) extends CompositePlayer[Player](_spec) {

  override protected val wrapped: Seq[Player] = {
    playerKeys map { case (p, k) => new KeyActivated(k, p, PlayerSpec(visible = false)) }
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
