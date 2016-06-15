package music2.player

/**
  * Takes multiple `Player`s, and outputs all of them
  * @param children the `Player`s to take output from
  */
class Combiner(val children: Seq[Player], _spec: PlayerSpec = PlayerSpec()) extends Player(_spec) {
  override protected def _play: Playable = {
    (children map (_.play)) average
  }
}
