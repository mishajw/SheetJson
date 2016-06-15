package music2.output
import java.io.{BufferedOutputStream, FileOutputStream}

import music2.player.Playable

/**
  * Writes played data to a file
  * @param path the file to write to
  */
class FileOut(val path: String) extends Out {

  /**
    * Stream to write bytes to
    */
  private val pw = new BufferedOutputStream(new FileOutputStream(path))

  override def play(p: Playable): Unit = {
    pw.write(p.toBytes.toArray)
  }

  /**
    * Stop writing
    */
  def stop() = {
    pw.close()
  }
}
