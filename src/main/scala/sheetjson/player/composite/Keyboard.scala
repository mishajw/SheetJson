package sheetjson.player.composite

import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.filter.KeyActivated
import sheetjson.player._

class Keyboard(playerKeys: Map[KeyCode, ActivatablePlayer],
               _spec: PlayerSpec) extends CompositePlayer[Player](_spec) with ListenerPlayer {

  override val keys: Seq[KeyCode] = playerKeys.toSeq map { case (k, _) => k }

  override protected val wrapped: Seq[Player] =
    playerKeys.toSeq collect { case (_, p) => p.asInstanceOf[Player] }

  override protected def extract(t: Player): Player = t

  override protected def _play: Playable = {
    wrapped.map(_.play).combine
  }

  override def keyPressed(kc: KeyCode): Unit = playerKeys(kc).activate()

  override def keyReleased(kc: KeyCode): Unit = playerKeys(kc).deactivate()

}
