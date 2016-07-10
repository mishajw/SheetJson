package sheetjson.output

import sheetjson.player.Playable

/**
  * Objects extending this take playable values and do something with them
  */
trait Out {

  private var _playing = true

  def playing = _playing

  /**
    * Play to an output
 *
    * @param p the data to play
    */
  def play(p: Playable): Unit

  /**
    * Called when reached the end of input
    */
  def reachedEnd() {
    _playing = false
  }
}
