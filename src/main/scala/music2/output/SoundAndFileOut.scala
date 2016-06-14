package music2.output
import music2.player.PlayableImplicits.Playable

class SoundAndFileOut(val path: String) extends Out {

  val fileOut = new FileOut(path)

  override def play[T](x: T)(implicit p: Playable[T]): Unit = {
    fileOut.play(x)
    SoundOut.play(x)
  }
}
