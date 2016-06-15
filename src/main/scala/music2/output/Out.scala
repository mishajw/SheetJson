package music2.output

import music2.player.Playable

/**
  * Objects extending this take playable values and do something with them
  */
trait Out {
  /**
    * Play to an output
    * @param p the data to play
    */
  def play(p: Playable): Unit
}
