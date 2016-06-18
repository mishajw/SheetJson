package music2.player.composite

import music2.player.composite.Riff.{PlayerDescription, PlayerDuration, PlayerSpan}
import music2.player.{Playable, Player, PlayerSpec}
import music2.util.Time.Bars

/**
  * Plays a sequence of notes for certain time spans
 *
  * @param _notes the notes and their spanning times
  */
class Riff( _notes: Seq[PlayerDescription],
            _spec: PlayerSpec = PlayerSpec()) extends CompositePlayer[PlayerSpan](_spec) {

  /**
    * Cast all notes to `PlayerSpan`
    */
  override protected val wrapped: Seq[PlayerSpan] = {
    var cumulativeTime: Bars = Bars(0)

    for (n <- _notes) yield n match {
      case ps: PlayerSpan =>
        cumulativeTime = ps.end ; ps
      case PlayerDuration(p, d) =>
        val ps = PlayerSpan(p, cumulativeTime, cumulativeTime + d)
        cumulativeTime = cumulativeTime + d
        ps
    }
  }

  /**
    * The total duration of the riffs
    */
  private lazy val riffDuration = wrapped.map(_.end).maxBy(_._value)

  override protected def _play: Playable = {
    val progress = Bars(step) % riffDuration

    val playingNotes = wrapped
      .filter(n => n.start <= progress && progress <= n.end)
      .map(_.player)

    playingNotes.map(_.play) combine
  }

  override protected def extract(p: PlayerSpan): Player = p.player
}

/**
  * Singleton holding types used in `Riff`
  */
object Riff {

  /**
    * Description of when a `Player` should play within a `Riff`
    */
  sealed trait PlayerDescription

  /**
    * @param player the `Player` to play
    * @param duration how long to play
    */
  case class PlayerDuration(player: Player, duration: Bars) extends PlayerDescription

  /**
    * @param player the `Player` to play
    * @param start when to start playing
    * @param end when to finish playing
    */
  case class PlayerSpan(player: Player, start: Bars, end: Bars) extends PlayerDescription
}
