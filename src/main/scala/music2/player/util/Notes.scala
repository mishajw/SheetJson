package music2.player.util

/**
  * Represents the notes and frequencies
  */
object Notes {

  /**
    * Note types
    */
  sealed trait Note

  sealed trait RelativeNote extends Note
  case object C extends RelativeNote ; case object Cs extends RelativeNote
  case object D extends RelativeNote ; case object Ds extends RelativeNote
  case object E extends RelativeNote
  case object F extends RelativeNote ; case object Fs extends RelativeNote
  case object G extends RelativeNote ; case object Gs extends RelativeNote
  case object A extends RelativeNote ; case object As extends RelativeNote
  case object B extends RelativeNote

  case class AbsoluteNote(note: RelativeNote, octave: Int) extends Note {
    def this(note: RelativeNote) = this(note, 4)
  }

  /**
    * Alias flat notes to existing sharp notes
    */
  val Df = Cs
  val Ef = Ds
  val Gf = Fs
  val Af = Gs
  val Bf = As

  def successor(n: RelativeNote): RelativeNote = n match {
    case C => Cs ; case Cs => D
    case D => Ds ; case Ds => E
    case E => F
    case F => Fs ; case Fs => G
    case G => Gs ; case Gs => A
    case A => As ; case As => B
    case B => C
  }

  def successor(on: AbsoluteNote): AbsoluteNote = on match {
    case AbsoluteNote(B, o) => AbsoluteNote(successor(B), o + 1)
    case AbsoluteNote(n, o) => AbsoluteNote(successor(n), o)
  }
}
