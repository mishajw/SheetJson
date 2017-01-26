package sheetjson.player.origin

import java.io.File
import javax.sound.sampled.{AudioInputStream, AudioSystem}

import sheetjson.output.SoundOut
import sheetjson.player.{Playable, Player, PlayerSpec}
import sheetjson.util.Config

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

class RawFile(path: String, _spec: PlayerSpec) extends OriginPlayer(_spec) {

  /**
    * Playables taken from the input file
    */
  private val playablesFromFile: Vector[Playable] = {
    val rawInput = AudioSystem.getAudioInputStream(new File(path))

    // Change the input so that it's the same format as the rest of the system
    val formattedInput: AudioInputStream = AudioSystem.getAudioInputStream(Config.format, rawInput)

    // Read in the bytes
    val bytes: ArrayBuffer[Byte] = new ArrayBuffer()
    breakable { while (true) {
      val buffer = Array.fill(32){0.toByte}

      formattedInput.read(buffer) match {
        case -1 => break()
        case n => bytes ++= buffer.take(n)
      }
    }}

    // Format the bytes to be playables
    bytes
      .grouped(2)
      .map(Playable.fromBytes)
      .map(_.getOrElse(Playable.default))
      .toVector
  }

  /**
    * The size of the Playables list. Expensive to compute with large files, so is stored here.
    */
  private lazy val playablesSize = playablesFromFile.size

  override protected def _play: Playable = {
    playablesFromFile(absoluteStep.toInt % playablesSize)
  }
}
