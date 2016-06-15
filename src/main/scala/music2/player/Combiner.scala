package music2.player

/**
  * Takes multiple `Player`s, and outputs all of them
  * @param _components the `Player`s to take output from
  */
class Combiner(_components: Seq[Player], _spec: PlayerSpec = PlayerSpec()) extends CompositePlayer(_spec) {

  override protected def _play: Playable = {
    (components map (_.play)) average
  }

  override def components: Seq[Player] = _components
}
