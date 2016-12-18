package sheetjson.player.composite

import sheetjson.player.activatable.IncrementalInteractivePlayer
import sheetjson.player.activatable.IncrementalInteractivePlayer.IncrementalInteractiveSpec
import sheetjson.player.{Playable, Player, PlayerSpec}

class Scroller(components: Seq[Player],
               _spec: PlayerSpec,
               override val interactiveSpec: IncrementalInteractiveSpec)
    extends CompositePlayer[Player](_spec) with IncrementalInteractivePlayer {

  override protected val wrapped: Seq[Player] = components

  override protected def extract(t: Player): Player = t

  private var componentIndex = 0

  override protected def _play: Playable = {
    components(componentIndex).play
  }

  override def next(): Unit = {
    componentIndex += 1
    maskComponentIndex()
  }

  override def previous(): Unit = {
    componentIndex -= 1
    maskComponentIndex()
  }

  private def maskComponentIndex(): Unit = {
    if (components.length <= componentIndex)
      componentIndex = 0
    else if (componentIndex < 0)
      componentIndex = components.length - 1
  }
}
