package music2.player.util

import music2.player.util.Notes._

/**
  * Holds the scales for notes
  */
object Scales {

  /**
    * Scales
    */
  sealed abstract class Scale(val ns: RelativeNote*)

  case object Major extends Scale(C, D, E, Fs, A, B)
  case object Blues extends Scale(C, Ds, F, G, As)
  case object BluesExt extends Scale(C, Ds, F, Fs, G, As)

  def get(n: RelativeNote, s: Scale) = translate(n, s.ns)

  /**
    * Translate a scale to start at a note
 *
    * @param n the note to start at
    * @param ns the existing scale
    * @return the translated scale
    */
  private def translate(n: RelativeNote, ns: Seq[RelativeNote]): Seq[RelativeNote] = {
    val incr = findIncrement(C, n)

    // Call successor `incr` times on `ns`
    (0 until incr).foldRight(ns)((_, xs) => xs.map(successor))
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
    case false => 1 + findIncrement(successor(n1), n2)
  }
}
