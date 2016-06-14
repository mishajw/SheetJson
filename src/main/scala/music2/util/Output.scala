package music2.util

import java.util.concurrent.LinkedBlockingQueue
import javax.sound.sampled._

import music2.player.PlayableImplicits.Playable

import scala.collection.JavaConverters._

object Output {

  val sampleRate = 24000

  /**
    * Input audio is read in these size chunks
    */
  private val bufferAmount = 64

  /**
    * Bytes are read from this queue
    */
  private val byteQueue =
    new LinkedBlockingQueue[Byte]()

  /**
    * The thread that plays bytes
    */
  private val thread = new Thread(new Runnable {
    def run() = playFromQueue()
  })

  /**
    * @return the default audio line
    */
  private def defaultLine = {
    val format =
      // TODO: Check the parameters
      new AudioFormat(sampleRate, 32, 1, true, false)

    val info =
      new DataLine.Info(classOf[SourceDataLine], format)

    val line =
      AudioSystem.getLine(info).asInstanceOf[SourceDataLine]

    line.open(format, 4096)
    line.start()

    line
  }

  /**
    * Destroy a line
    * @param line
    */
  private def destroy(line: SourceDataLine) = {
    line.drain()
    line.stop()
    line.close()
  }

  /**
    * Read from the byte queue, and play the results
    */
  private def playFromQueue() = {
    implicit val line = defaultLine
    var running = true

    while (running) {
      try {
        playBytes({
          for (_ <- 0 to bufferAmount) yield {
            byteQueue.take()
          }
        })
      } catch {
        case e: InterruptedException =>
          running = false
      }
    }

    println("Done")

    destroy(line)
  }

  /**
    * Play bytes to audio
    * @param bytes the bytes to play
    * @param line the line to play them from
    */
  private def playBytes(bytes: Traversable[Byte])(implicit line: SourceDataLine) = {
    line.write(bytes.toArray, 0, bytes.size)
  }

  /**
    * Start the playing
    */
  def start(): Unit = thread.start()

  /**
    * Play an integer
    */
  def play(x: Int) = byteQueue add x.toByte

  /**
    * Stop the playing
    */
  def stop() = thread.synchronized {
    thread.interrupt()
    thread.join()
  }
}
