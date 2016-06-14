package music2

import music2.management.Composer
import music2.player.Combiner
import music2.player.util.Frequencies
import music2.player.util.Notes._

object Music2 {
  def main(args: Array[String]) {

    val st1 = Frequencies get C
    val st2 = Frequencies get E
    val st3 = Frequencies get G

    val comb = new Combiner(st1, st2, st3)

    Composer play (comb, 3)
  }
}
