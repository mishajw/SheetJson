package music2.output

import music2.player.PlayableImplicits.Playable

trait Out {
  def play[T](x: T)(implicit p: Playable[T]): Unit
}
