package music2.player.filter

import music2.player.{Playable, Player, PlayerSpec}

class Randomizer(_child: Player, _spec: PlayerSpec = PlayerSpec()) extends FilterPlayer(_child, _spec) {
  override protected def _play: Playable = {
    child.play * (1 + Math.random() * 0.3)
  }
}
