package sheetjson.player.composite

import sheetjson.player.{Player, PlayerSpec}
import sheetjson.util.Identifiable

abstract class CompositePlayer[T](_spec: PlayerSpec) extends Player(_spec) {

  /**
    * A list of `Player`s, wrapped in some way
    */
  protected val wrapped: Seq[T]

  /**
    * Extract the `Player` from a wrapped value
 *
    * @param t the wrapped value
    * @return the player
    */
  protected def extract(t: T): Player

  /**
    * Get all `Player`s
 *
    * @return
    */
  def components: Seq[Player] = wrapped map extract

  override def childrenAlive: Boolean = components.forall(_.alive)

  override def identifiableChildren: Seq[Identifiable] = super.identifiableChildren ++ components
}
