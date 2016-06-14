package music2.player
import music2.player.PlayableImplicits.Playable

class Combiner[T](val children: Player[T]*) extends Player[T] {
  override protected def _play(implicit p: Playable[T]): T = {
    val played = children map (_.play)
    val average = played.map(p.toScale).sum / played.size

    p.fromScale(average)
  }
}
