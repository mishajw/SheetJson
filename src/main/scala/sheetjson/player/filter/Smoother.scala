package sheetjson.player.filter

import sheetjson.player.{Playable, Player, PlayerSpec}

import scala.collection.mutable

class Smoother( _child: Player,
                val smoothness: Double,
                _spec: PlayerSpec) extends FilterPlayer(_child, _spec) {

  /**
    * A queue of past notes played
    */
  private val past = mutable.Queue[Playable]()

  /**
    * The maximum amount in the queue
    */
  private val queueMax: Int = (smoothness * 1000).toInt

  override protected def _play: Playable = {
    val played = child.play

    past += played

    while (past.size > queueMax)
      past.dequeue

    past.combine
  }
}
