package music2.player

abstract class CompositePlayer(_spec: PlayerSpec = PlayerSpec()) extends Player(_spec) {

  def components: Seq[Player]

}
