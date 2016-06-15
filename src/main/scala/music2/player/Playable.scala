package music2.player

class Playable(private val value: Double) {
  def toBytes: Seq[Byte] = ???
}

object Playable {

  def apply(value: Double) = new Playable(value)

  implicit class PlayableCollection(val col: Traversable[Playable]) extends AnyVal {
    def average: Playable = Playable(col.map(_.value).sum / col.size)
  }

  val default = new Playable(0)

}
