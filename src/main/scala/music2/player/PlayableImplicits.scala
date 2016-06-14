package music2.player

object PlayableImplicits {
  trait Playable[T] {
    val max: T
    val min: T

    def toInt(x: T): Int

    def scale(x: T): Int = (toInt(x) - toInt(min)) / (toInt(max) - toInt(min))
  }

  implicit object PlayableInt extends Playable[Int] {
    override val max: Int = Int.MaxValue
    override val min: Int = Int.MinValue

    override def toInt(x: Int): Int = x
  }

  implicit object PlayableDouble extends Playable[Double] {
    override val max: Double = Double.MaxValue
    override val min: Double = Double.MinValue

    override def toInt(x: Double): Int = x.toInt
  }
}
