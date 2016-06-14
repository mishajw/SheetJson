package music2.player

object PlayableImplicits {
  trait Playable[T] {
    val max: T
    val min: T

    def toScale(x: T): Double
    def fromScale(s: Double): T

    def to[V](x: T)(implicit p: Playable[V]): V = {
      p.fromScale(toScale(x))
    }
  }

  implicit object PlayableInt16 extends Playable[Int] {
    override val max: Int = 2 ^ 15 - 1
    override val min: Int = -2 ^ 15

    override def toScale(x: Int): Double = (x - min) / (max - min)

    override def fromScale(s: Double): Int = (((max - min) * s) + min).asInstanceOf[Int]
  }

  implicit object PlayableDouble extends Playable[Double] {
    override val max: Double = 1
    override val min: Double = -1

    override def toScale(x: Double): Double = (x + 1) / 2

    override def fromScale(s: Double): Double = (s * 2) - 1
  }
}
