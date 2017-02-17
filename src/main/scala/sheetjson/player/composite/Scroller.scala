package sheetjson.player.composite

import sheetjson.player.listener.{IncrementableListener, Listener, ListenerPlayer}
import sheetjson.player.{Playable, Player, PlayerSpec}

class Scroller(components: Seq[Player],
               _spec: PlayerSpec)
    extends CompositePlayer[Player](_spec) with ListenerPlayer with IncrementableListener {

  override protected val wrapped: Seq[Player] = components

  override protected def extract(t: Player): Player = t

  private var componentIndex = 0

  override protected def _play: Playable = {
    components(componentIndex).play
  }

  private def maskComponentIndex(): Unit = {
    if (components.length <= componentIndex)
      componentIndex = 0
    else if (componentIndex < 0)
      componentIndex = components.length - 1
  }

  override val listeners: Seq[Listener] = Seq(this)

  override def next(): Unit = {
    componentIndex += 1
    maskComponentIndex()
  }

  override def previous(): Unit = {
    componentIndex -= 1
    maskComponentIndex()
  }

  override def displayParameters: Seq[Object] = Seq(components(componentIndex).toString)
}
