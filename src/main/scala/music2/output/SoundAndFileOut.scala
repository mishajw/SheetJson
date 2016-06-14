package music2.output
import music2.player.PlayableImplicits.Playable

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

  override def play[T](x: T)(implicit p: Playable[T]): Unit = {
    fileOut.play(x)
    SoundOut.play(x)
  }

  /**
    * Stop writing out
    */
  def stop() = {
    SoundOut.stop()
    fileOut.stop()
  }
}
