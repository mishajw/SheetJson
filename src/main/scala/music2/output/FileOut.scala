package music2.output
import java.io.{BufferedOutputStream, FileOutputStream}

import music2.player.PlayableImplicits.Playable

class FileOut(val path: String) extends Out {

  /**
    * Stream to write bytes to
    */
  private val pw = new BufferedOutputStream(new FileOutputStream(path))

  override def play[T](x: T)(implicit p: Playable[T]): Unit = {
    pw.write(p.toBytes(x).toArray)
  }

  /**
    * Stop writing
    */
  def stop() = {
    pw.close()
  }
}
