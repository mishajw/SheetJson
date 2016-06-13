package music2.util

import javax.sound.sampled.{AudioFormat, AudioSystem, DataLine, SourceDataLine}

object Output {

  private val format =
    // TODO: Check the parameters
    new AudioFormat(24000, 16, 1, true, false)

  private val info =
    new DataLine.Info(classOf[SourceDataLine], format)

  private val line =
    AudioSystem.getLine(info).asInstanceOf[SourceDataLine]

  def start() = {
    line.open(format, 4096)
    line.start()
  }

  def stop() = {
    line.drain()
    line.stop()
    line.close()
  }

  def play(x: Double) = {
    val amount = 64
    val fullAng = Math.PI * 2

    val buffer = (for {
      ang <- fullAng to 0 by (-fullAng / amount)
    } yield {
      Math.round(ang * 32767)
    }).map(_.asInstanceOf[Byte])
      .toArray

    println((Math.PI * 2) to 0 by (-Math.PI / amount))
    println(buffer.length)
    println(buffer.mkString(","))

    println("Start")
    for (_ <- 0 to 500)
      line.write(buffer, 0, amount)
    println("End")
  }
}
