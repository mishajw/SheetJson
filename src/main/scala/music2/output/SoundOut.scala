package music2.output

import java.util.concurrent.LinkedBlockingQueue
import javax.sound.sampled.{AudioFormat, AudioSystem, DataLine, SourceDataLine}

import music2.player.{EndPlayable, Playable}
import music2.sampleRate

import scala.collection.mutable.ArrayBuffer

/**
  * Takes sound data and plays them through an audio device
  */
class SoundOut extends Out {

  /**
    * Input audio is read in these size chunks
    */
  private val bufferAmount = 64

  /**
    * Bytes are read from this queue
    */
  private val byteQueue =
    new LinkedBlockingQueue[Playable]()

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
      new AudioFormat(sampleRate, 16, 1, true, false)

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

    val bytes: ArrayBuffer[Byte] = ArrayBuffer()
    while (running) {
      val playable = byteQueue.take()

      if (playable.isInstanceOf[EndPlayable]) {
        running = false
        reachedEnd()
      }

      bytes ++= playable.toBytes

      if (bytes.size > bufferAmount) {
        playBytes(bytes)
        bytes.clear()
      }
    }

    destroy(line)
  }

  /**
    * Play bytes to audio
    *
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
    * Play a playable value
    */
  def play(p: Playable) = {
    byteQueue add p
  }

  /**
    * Stop the playing
    */
  def stop() = /*thread.synchronized */{
    thread.join()
  }
}
