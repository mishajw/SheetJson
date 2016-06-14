package music2

import music2.player.SimpleTone
import music2.util.Output

object Music2 {
  def main(args: Array[String]) {
    val st = new SimpleTone(261.63)

    val played = for (_ <- 0 until Output.sampleRate * 3)
      yield st.play

    played.foreach(Output.play(_))

    Output start()
    Thread.sleep(5000)
    Output stop()
  }
}
