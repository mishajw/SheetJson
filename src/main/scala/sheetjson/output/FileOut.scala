package sheetjson.output
import java.io.{BufferedOutputStream, FileOutputStream}

import sheetjson.player.{EndPlayable, Playable}

/**
  * Writes played data to a file
 *
  * @param path the file to write to
  */
class FileOut(val path: String) extends Out {

  /**
    * Stream to write bytes to
    */
  private val pw = new BufferedOutputStream(new FileOutputStream(path))

  override def play(p: Playable): Unit = {
    pw.write(p.toBytes.toArray)

    if (p.isInstanceOf[EndPlayable])
      reachedEnd()
  }

  /**
    * Stop writing
    */
  def stop() = {
    pw.close()
  }
}
