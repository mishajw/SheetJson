package music2.player.filter

import music2.player.{Playable, Player, PlayerSpec}

import scala.collection.mutable

class Smoother( _child: Player,
                val smoothness: Double,
                _spec: PlayerSpec) extends FilterPlayer(_child, _spec) {

  val past = mutable.Queue[Playable]()

  private val queueMax: Int = smoothness.toInt

  override protected def _play: Playable = {
    val played = child.play

    past += played

    while (past.size > queueMax)
      past.dequeue

    past average
  }
}
