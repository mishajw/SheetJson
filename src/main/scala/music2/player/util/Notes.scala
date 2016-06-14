package music2.player.util

/**
  * Represents the notes and frequencies
  */
object Notes {

  /**
    * Note types
    */
  sealed trait Note
  case object C extends Note ; case object Cs extends Note
  case object D extends Note ; case object Ds extends Note
  case object E extends Note
  case object F extends Note ; case object Fs extends Note
  case object G extends Note ; case object Gs extends Note
  case object A extends Note ; case object As extends Note
  case object B extends Note

  case class OctaveNote(note: Note, octave: Int)

  /**
    * Alias flat notes to existing sharp notes
    */
  val Df = Cs
  val Ef = Ds
  val Gf = Fs
  val Af = Gs
  val Bf = As

  def successor(n: Note): Note = n match {
    case C  => Cs
    case Cs => D
    case D  => Ds
    case Ds => E
    case E  => F
    case F  => Fs
    case Fs => G
    case G  => Gs
    case Gs => A
    case A  => As
    case As => B
    case B  => C
  }
}
