package sheetjson.output

import sheetjson.player.Playable

/**
  * Combines FileOut and SoundOut
 *
  * @param path to file to write to
  */
class SoundAndFileOut(val path: String) extends Out {

  /**
    * The FileOut object used
    */
  val fileOut = new FileOut(path)

  /**
    * The SoundOut object used
    */
  val soundOut = new SoundOut()

  /**
    * Start writing out
    */
  def start() = {
    soundOut.start()
  }

  override def play(p: Playable): Unit = {
    fileOut.play(p)
    soundOut.play(p)
  }


  override def playing: Boolean = {
    fileOut.playing || soundOut.playing
  }

  /**
    * Stop writing out
    */
  def stop() = {
    soundOut.stop()
    fileOut.stop()
  }
}
