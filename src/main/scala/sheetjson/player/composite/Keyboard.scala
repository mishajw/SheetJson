package sheetjson.player.composite

import sheetjson.input.KeyListener.KeyCode
import sheetjson.player._
import sheetjson.player.activatable.InteractivePlayer.InteractiveSpec
import sheetjson.player.activatable.MultiKeyInteractivePlayer.MultiKeyInteractiveSpec
import sheetjson.player.activatable.{MultiKeyInteractivePlayer, SingleKeyInteractivePlayer}

class Keyboard(components: Seq[SingleKeyInteractivePlayer],
               _spec: PlayerSpec,
               override val interactiveSpec: MultiKeyInteractiveSpec)
    extends CompositePlayer[Player](_spec) with MultiKeyInteractivePlayer {

  override protected val wrapped: Seq[Player] =
    components collect { case p: Player => p }

  override protected def extract(t: Player): Player = t

  override protected def _play: Playable = {
    wrapped.map(_.play).combine
  }

  override def activate(i: KeyCode): Unit = components(i).activate()

  override def deactivate(i: KeyCode): Unit = components(i).deactivate()

}
