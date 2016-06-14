package music2.player
import music2.LifeTime
import music2.player.PlayableImplicits.Playable

/**
  * Plays a sequence of notes for certain durations
  * @param notes the notes and their durations
  */
class Riff[T](notes: Seq[(Player[T], Double)], _lifeTime: LifeTime = None) extends Player[T](_lifeTime) {

  /**
    * The total duration of the riffs
    */
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
