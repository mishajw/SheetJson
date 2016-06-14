package music2

import music2.output.{Out, SoundOut}
import music2.player.SimpleTone

object Music2 {
  def main(args: Array[String]) {

    implicit val out: Out = SoundOut

    val st = new SimpleTone(261.63)

    val played = for (_ <- 0 until sampleRate * 3)
      yield st.play

    played.foreach(SoundOut.play(_))

    SoundOut start()
    Thread.sleep(5000)
    SoundOut stop()
  }
}
