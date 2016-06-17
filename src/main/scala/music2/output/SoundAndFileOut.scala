package music2.output

import music2.player.Playable

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
    * Start writing out
    */
  def start() = {
    SoundOut.start()
  }

  override def play(p: Playable): Unit = {
    fileOut.play(p)
    SoundOut.play(p)
  }


  override def playing: Boolean = {
    fileOut.playing || SoundOut.playing
  }

  /**
    * Stop writing out
    */
  def stop() = {
    SoundOut.stop()
    fileOut.stop()
  }
}
