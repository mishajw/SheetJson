package music2.output

import music2.player.PlayableImplicits.Playable

trait Out {
  /**
    * Play to an output
    * @param x the data to play
    */
  def play[T](x: T)(implicit p: Playable[T]): Unit
}
