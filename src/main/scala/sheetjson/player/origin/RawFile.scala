package sheetjson.player.origin

import java.io.File
import javax.sound.sampled.{AudioInputStream, AudioSystem}

import sheetjson.player.{Playable, PlayerSpec}
import sheetjson.util.Config

import scala.collection.mutable.ArrayBuffer

class RawFile(path: String, _spec: PlayerSpec) extends OriginPlayer(_spec) {

  val rawInput = AudioSystem.getAudioInputStream(new File(path))

  // Change the input so that it's the same format as the rest of the system
  val formattedInput: AudioInputStream = AudioSystem.getAudioInputStream(Config.format, rawInput)

  /**
    * Bytes taken from the file, yet to be parsed into playables
    */
  val bytesFromFile: ArrayBuffer[Byte] = ArrayBuffer()

  /**
    * Playables parsed from the bytes
    */
  val playablesFromFile: ArrayBuffer[Playable] = ArrayBuffer()

  /**
    * The size of the playables. Should be incremented whenever a playable is added
    */
  var playablesSize: Int = 0

  /**
    * True if there are no more bytes to be read from the file
    */
  var doneReading = false

  override protected def _play: Playable = {
    while (!doneReading && playablesSize <= absoluteStep.toInt) {
      copyFileToBytes()
      copyBytesToPlayable()
    }

    playablesFromFile(absoluteStep.toInt)
  }

  /**
    * Copy bytes from the file into `bytesFromFile`
    */
  private def copyFileToBytes(): Unit = {
    val buffer = Array.fill(32){0.toByte}

    formattedInput.read(buffer) match {
      case -1 => doneReading = true
      case n => bytesFromFile ++= buffer.take(n)
    }
  }

  /**
    * Copy from `bytesFromFile` and parse into `Playable`s and put into `playablesFromFile`
    */
  private def copyBytesToPlayable(): Unit = {
    val remainingBytes = ArrayBuffer[Byte]()

    val newPlayables = bytesFromFile
      .grouped(2)
      .map {
        case Seq(a, b) =>
          Playable.fromBytes(Seq(a, b))
            .getOrElse(Playable.default)
        case other =>
          remainingBytes ++= other
          Playable.default
      }
      .toSeq

    playablesFromFile ++= newPlayables
    playablesSize += newPlayables.size

    bytesFromFile.clear()
    bytesFromFile ++= remainingBytes
  }
}
