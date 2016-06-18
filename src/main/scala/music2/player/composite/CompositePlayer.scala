package music2.player.composite

import music2.player.{Player, PlayerSpec}

abstract class CompositePlayer[T](_spec: PlayerSpec) extends Player(_spec) {

  /**
    * A list of `Player`s, wrapped in some way
    */
  protected val wrapped: Seq[T]

  /**
    * Extract the `Player` from a wrapped value
    * @param t the wrapped value
    * @return the player
    */
  protected def extract(t: T): Player

  /**
    * Get all `Player`s
    * @return
    */
  def components: Seq[Player] = wrapped map extract

  override def childrenAlive: Boolean = components.forall(_.alive)

}
