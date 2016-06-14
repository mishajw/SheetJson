package music2.player.util

import music2.player.util.Notes._

object Scales {
  def major(n: Note): Seq[Note] =
    translate(n, Seq(C, D, E, Fs, A, B))

  def blues(n: Note): Seq[Note] =
    translate(n, Seq(C, Ds, F, G, As))

  def bluesExt(n: Note): Seq[Note] =
    translate(n, Seq(C, Ds, F, Fs, G, As))

  private def translate(n: Note, ns: Seq[Note]): Seq[Note] = {
    val incr = findIncrement(C, n)

    // Call successor `incr` times on `ns`
    (0 until incr).foldRight(ns)((_, xs) => xs.map(successor))
  }

  private def findIncrement(n1: Note, n2: Note): Int = n1 == n2 match {
    case true => 0
    case false => 1 + findIncrement(successor(n1), n2)
  }
}
