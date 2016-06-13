package music2.util

import java.util.concurrent.LinkedBlockingQueue
import javax.sound.sampled._

object Output {

  private val bufferAmount = 64

  private val byteQueue =
    new LinkedBlockingQueue[Byte]()

  private val thread = new Thread(new Runnable {
    def run() = playFromQueue()
  })

  private def defaultLine = {
    val format =
      // TODO: Check the parameters
      new AudioFormat(24000, 16, 1, true, false)

    val info =
      new DataLine.Info(classOf[SourceDataLine], format)

    val line =
      AudioSystem.getLine(info).asInstanceOf[SourceDataLine]

    line.open(format, 4096)
    line.start()

    line
  }

  private def destroy(line: SourceDataLine) = {
    line.drain()
    line.stop()
    line.close()
  }

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

  private def playBytes(bytes: Traversable[Byte])(implicit line: SourceDataLine) =
    line.write(bytes.toArray, 0, bytes.size - 1)

  def start(): Unit = thread.start()

  def play(x: Int) = byteQueue add x.toByte

  def stop() = thread.synchronized {
    thread.interrupt()
    thread.join()
  }
}
