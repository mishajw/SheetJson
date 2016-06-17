package music2.player.composite

import music2.player.{Playable, Player, PlayerSpec}

/**
  * Takes multiple `Player`s, and outputs all of them
  *
  * @param _components the `Player`s to take output from
  */
class Combiner(_components: Seq[Player], _spec: PlayerSpec = PlayerSpec()) extends CompositePlayer[Player](_spec) {

  override protected def _play: Playable = {
    (components map (_.play)) combine
  }

  override def components: Seq[Player] = _components

  override protected val wrapped: Seq[Player] = _components

  override protected def extract(p: Player): Player = p
}
