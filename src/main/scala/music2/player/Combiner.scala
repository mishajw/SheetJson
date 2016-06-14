package music2.player
import music2.LifeTime
import music2.player.PlayableImplicits.Playable

/**
  * Takes multiple `Player`s, and outputs all of them
  * @param children the `Player`s to take output from
  */
class Combiner[T](val children: Seq[Player[T]], _lifeTime: LifeTime = None) extends Player[T](_lifeTime) {
  override protected def _play(implicit p: Playable[T]): T = {
    val played = children map (_.play)
    val average = played.map(p.toScale).sum / played.size

    p.fromScale(average)
  }
}
