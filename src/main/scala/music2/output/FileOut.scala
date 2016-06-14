package music2.output
import music2.player.PlayableImplicits.Playable

class FileOut(val path: String) extends Out {
  override def play[T](x: T)(implicit p: Playable[T]): Unit = ???
}
