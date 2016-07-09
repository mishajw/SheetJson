package music2.player.origin

import java.io.File
import java.nio.{ByteBuffer, ByteOrder}
import javax.sound.sampled.{AudioInputStream, AudioSystem}

import music2.output.SoundOut
import music2.player.{Playable, Player, PlayerSpec}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

class RawFile(path: String, _spec: PlayerSpec) extends Player(_spec) {

  private val playablesFromFile: Vector[Playable] = {
    val rawInput = AudioSystem.getAudioInputStream(new File(path))
    val formattedInput: AudioInputStream = AudioSystem.getAudioInputStream(SoundOut.format, rawInput)

    val bytes: ArrayBuffer[Byte] = new ArrayBuffer()

    breakable { while (true) {
      val buffer = Array.fill(32){0.toByte}

      formattedInput.read(buffer) match {
        case -1 => break()
        case n => bytes ++= buffer.take(n)
      }
    }}

    bytes
      .grouped(2)
      .map { case bs =>
        val byteBuffer = ByteBuffer.wrap(bs.toArray).order(ByteOrder.LITTLE_ENDIAN)
        byteBuffer.getShort().toInt
      }
      .map(Playable.fromInt)
      .toVector
  }

  private lazy val playablesSize = playablesFromFile.size

  override protected def _play: Playable = {
    playablesFromFile(absoluteStep.toInt % playablesSize)
  }
}
