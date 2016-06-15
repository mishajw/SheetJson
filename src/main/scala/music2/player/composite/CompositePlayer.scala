package music2.player.composite

import music2.player.{Player, PlayerSpec}

abstract class CompositePlayer(_spec: PlayerSpec = PlayerSpec()) extends Player(_spec) {

  def components: Seq[Player]

}
