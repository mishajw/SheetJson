package music2.player

import music2.player.PlayableImplicits.Playable

class SimpleTone(val frequency: Double) extends Player[Int] {

  val fullAngle = Math.PI * 2

  private lazy val secondsLong = 1 / frequency

  protected def _play(implicit p: Playable[Int]): Int = {
    val progress = (step % secondsLong) / secondsLong
    val angle = progress * fullAngle

    (Math.sin(angle) * 32767).asInstanceOf[Int]
  }
}
