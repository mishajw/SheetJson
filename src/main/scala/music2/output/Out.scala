package music2.output

import music2.player.PlayableImplicits.Playable

/**
  * Objects extending this take playable values and do something with them
  */
trait Out {
  /**
    * Play to an output
    * @param x the data to play
    */
  def play[T](x: T)(implicit p: Playable[T]): Unit
}
