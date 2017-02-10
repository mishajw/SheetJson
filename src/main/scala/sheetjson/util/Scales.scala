package sheetjson.util

import sheetjson.util.Notes._

/**
  * Holds the scales for notes
  */
object Scales {

  /**
    * Scales
    */
  val scales = Map(
    "pentatonic" -> Seq(C, Cs, D, Ds, E, F, Fs, G, Gs, A, As, B),
    "major" -> Seq(C, D, E, F, G, A, B),
    "blues" -> Seq(C, Ds, F, G, A),
    "blues_ext" -> Seq(C, Ds, F, Fs, G, A)
  )

  def get(n: RelativeNote, s: String): Option[Seq[RelativeNote]] =
    (scales get s) map (translate(n, _))

  def get(n: RelativeNote, s: String, amount: Int, startOctave: Int = 5): Option[Seq[AbsoluteNote]] =
    get(n, s) map { rns =>
      Stream.continually(rns)
        .zipWithIndex
        .flatMap {
          case (rn, i) =>
            rn.map(AbsoluteNote(_, i + startOctave))
        }
    }

  /**
    * Translate a scale to start at a note
 *
    * @param n the note to start at
    * @param ns the existing scale
    * @return the translated scale
    */
  private def translate[T](n: RelativeNote, ns: Seq[RelativeNote]): Seq[RelativeNote] = {
    val incr = findIncrement(C, n)

    // Call successor `incr` times on `ns`
    (0 until incr).foldRight(ns)((_, xs) => xs.map(successors))
  }

  /**
    * Find how "far away" two notes are (e.g. C and E are 4 apart: C -> Cs -> D -> Ds -> E)
 *
    * @param n1 the first note
    * @param n2 the second note
    * @return the "distance" between notes
    */
  private def findIncrement(n1: RelativeNote, n2: RelativeNote): Int = n1 == n2 match {
    case true => 0
    case false => 1 + findIncrement(successors(n1), n2)
  }
}
