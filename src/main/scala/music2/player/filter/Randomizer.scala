package music2.player.filter

import music2.player.{Playable, Player, PlayerSpec}

class Randomizer( _child: Player,
                  val randomness: Double = 1.2,
                  _spec: PlayerSpec) extends FilterPlayer(_child, _spec) {

  override protected def _play: Playable = {
    child.play * (1 + Math.random() * randomness)
  }
}
