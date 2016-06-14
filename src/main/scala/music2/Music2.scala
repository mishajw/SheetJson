package music2

import music2.management.Composer
import music2.player.Notes._
import music2.player.{Combiner, Notes}

object Music2 {
  def main(args: Array[String]) {

    val st1 = Notes get C
    val st2 = Notes get E
    val st3 = Notes get G

    val comb = new Combiner(st1, st2, st3)

    Composer play (comb, 3)
  }
}
