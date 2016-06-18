package music2.util

import scala.util.Try

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

  /**
    * A note with no octave
 *
    * @param str the string representation of that note
    */
  sealed case class RelativeNote(str: String) extends Note {
    def ~==(n: Note) = n match {
      case rn: RelativeNote => equals(rn)
      case an: AbsoluteNote => equals(an.note)
    }
  }

  /**
    * The notes
    */
  val C = RelativeNote("C") ; val Cs = RelativeNote("Cs")
  val D = RelativeNote("D") ; val Ds = RelativeNote("Ds")
  val E = RelativeNote("E")
  val F = RelativeNote("F") ; val Fs = RelativeNote("Fs")
  val G = RelativeNote("G") ; val Gs = RelativeNote("Gs")
  val A = RelativeNote("A") ; val As = RelativeNote("As")
  val B = RelativeNote("B")

  /**
    * A list of all relative notes
    */
  private val allNotes = Seq(C, Cs, D, Ds, E, F, Fs, G, Gs, A, As, B)

  /**
    * A map of strings to their notes
    */
  private val noteStringMap = (allNotes map { rn => rn.str.toLowerCase -> rn }).toMap

  /**
    * A note that has an octave
 *
    * @param note the relative note
    * @param octave the octave
    */
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

  val rRelativeNote = """([A-Za-z]{1,2})""".r
  val rAbsoluteNote = """([A-Za-z]{1,2})([\-\d]+)""".r

  /**
    * Get a note by string representation
 *
    * @param str the string representation
    * @return the note for that string
    */
  def relativeNoteFor(str: String): Option[RelativeNote] =
    noteStringMap get str.toLowerCase
                         .replace("#", "s")

  def noteFor(str: String): Option[Note] = {
    val parts = str match {
      case rRelativeNote(key) =>
        (Notes relativeNoteFor key, None)
      case rAbsoluteNote(key, octave) =>
        (Notes relativeNoteFor key, Try(octave.toInt).toOption)
      case _ =>
        (None, None)
    }

    parts match {
      case (Some(key), Some(octave)) =>
        Some(AbsoluteNote(key, octave))
      case (keyOpt, _) => keyOpt
    }
  }

  /**
    * Map of note's successors
    */
  val successors = Map(
    C -> Cs, Cs -> D,
    D -> Ds, Ds -> E,
    E -> F,
    F -> Fs, Fs -> G,
    G -> Gs, Gs -> A,
    A -> As, As -> B,
    B -> C
  )

  /**
    * Map of a notes predecessors
    */
  val predecessors = successors.map({ case (k, v) => (v, k)})

  /**
    * Get the successor of an `AbsoluteNote`
    */
  def absoluteSuccessors(n: AbsoluteNote): AbsoluteNote = n match {
    case AbsoluteNote(B, o) => AbsoluteNote(successors(B), o + 1)
    case AbsoluteNote(n, o) => AbsoluteNote(successors(n), o)
  }

  /**
    * Get the predecessor of an `AbsoluteNote`
    */
  def absolutePredecessors(n: AbsoluteNote): AbsoluteNote = n match {
    case AbsoluteNote(B, o) => AbsoluteNote(predecessors(B), o - 1)
    case AbsoluteNote(n, o) => AbsoluteNote(predecessors(n), o)
  }
}
