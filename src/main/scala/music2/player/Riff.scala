package music2.player
import music2.player.PlayableImplicits.Playable

/**
  * Plays a sequence of notes for certain durations
  */
class Riff[T](notes: (Player[T], Double)*) extends Player[T] {

  private lazy val riffDuration = notes.map(_._2).sum

  override protected def _play(implicit p: Playable[T]): T = {
    var progress = step % riffDuration

    val player = notes.takeWhile({ case (_, d) =>
      if (progress < 0) {
        false
      } else {
        progress -= d
        true
      }
    }).last._1

    player.play
  }
}
