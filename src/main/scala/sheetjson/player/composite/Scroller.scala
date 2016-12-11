package sheetjson.player.composite

import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.{ListenerPlayer, Playable, Player, PlayerSpec}

class Scroller(nextKey: Int,
               previousKey: Int,
               components: Seq[Player],
               _spec: PlayerSpec) extends CompositePlayer[Player](_spec) with ListenerPlayer {

  override protected val wrapped: Seq[Player] = components

  override protected def extract(t: Player): Player = t

  override val keys: Seq[KeyCode] = Seq(nextKey, previousKey)

  private var componentIndex = 0

  override protected def _play: Playable = {
    components(componentIndex).play
  }

  override def keyPressed(kc: KeyCode): Unit = {
    if (kc == nextKey) componentIndex += 1
    if (kc == previousKey) componentIndex -= 1

    if (components.length <= componentIndex)
      componentIndex = 0
    else if (componentIndex < 0)
      componentIndex = components.length - 1
  }
}
