package music2.player.util

/**
  * Represents the notes and frequencies
  */
object Notes {

  /**
    * The default octave
    */
  val defaultOctave = 4

  /**
    * Note types
    */
  sealed trait Note {
    def ~==(n: Note): Boolean
  }

  sealed case class RelativeNote(str: String) extends Note {
    def ~==(n: Note) = n match {
      case rn: RelativeNote => equals(rn)
      case an: AbsoluteNote => equals(an.note)
    }
  }

  val C = RelativeNote("C") ; val Cs = RelativeNote("Cs")
  val D = RelativeNote("D") ; val Ds = RelativeNote("Ds")
  val E = RelativeNote("E")
  val F = RelativeNote("F") ; val Fs = RelativeNote("Fs")
  val G = RelativeNote("G") ; val Gs = RelativeNote("Gs")
  val A = RelativeNote("A") ; val As = RelativeNote("As")
  val B = RelativeNote("B")

  private val allNotes = Seq(C, Cs, D, Ds, E, F, Fs, G, Gs, A, As, B)

  private val noteStringMap = (allNotes map { rn => rn.str.toLowerCase -> rn }).toMap

  case class AbsoluteNote(note: RelativeNote, octave: Int) extends Note {
    def this(note: RelativeNote) = this(note, 4)
    def ~==(n: Note) = n match {
      case rn: RelativeNote => rn == note
      case an: AbsoluteNote => an.note == note
    }
  }

  /**
    * Alias flat notes to existing sharp notes
    */
  val Df = Cs
  val Ef = Ds
  val Gf = Fs
  val Af = Gs
  val Bf = As

  def noteFor(str: String): Option[Note] = {
    noteStringMap contains str.toLowerCase() match {
      case true => Some(noteStringMap(str))
      case false => None
    }
  }

  val successors = Map(
    C -> Cs, Cs -> D,
    D -> Ds, Ds -> E,
    E -> F,
    F -> Fs, Fs -> G,
    G -> Gs, Gs -> A,
    A -> As, As -> B,
    B -> C
  )

  val precessors = successors.map({ case (k, v) => (v, k)})

  def absoluteSuccessors(n: AbsoluteNote): AbsoluteNote = n match {
    case AbsoluteNote(B, o) => AbsoluteNote(successors(B), o + 1)
    case AbsoluteNote(n, o) => AbsoluteNote(successors(n), o)
  }

  def absolutePrecessors(n: AbsoluteNote): AbsoluteNote = n match {
    case AbsoluteNote(B, o) => AbsoluteNote(precessors(B), o - 1)
    case AbsoluteNote(n, o) => AbsoluteNote(precessors(n), o)
  }
}
