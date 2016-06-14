package music2.player

/**
  * Contains implicits for Playable values
  * Includes `Int` and `Double`
  */
object PlayableImplicits {
  trait Playable[T] {
    /**
      * The maximum and minimum values of the value
      */
    val max, min: T

    /**
      * Translate to a scaled value
      * @param x the value to translate
      * @return the scale as a `Double`
      */
    def toScale(x: T): Double

    /**
      * Translate a scale to a Playable value
      * @param s the scale as a `Double`
      * @return the Playable value
      */
    def fromScale(s: Double): T

    /**
      * Translate this playable value to another playable value
      * @param x the value to translate
      * @tparam V the type to translate to
      * @return the translated value
      */
    def to[V](x: T)(implicit p: Playable[V]): V = {
      p.fromScale(toScale(x))
    }

    /**
      * Translate this playable value to a byte array
      * @param x the value
      * @return the byte array
      */
    def toBytes(x: T): Seq[Byte] =
      PlayableInt16.toBytes(to[Int](x))
  }

  /**
    * Implicit for 16-bit integers
    */
  implicit object PlayableInt16 extends Playable[Int] {
    override val max: Int = 2 ^ 15 - 1
    override val min: Int = -2 ^ 15

    override def toScale(x: Int): Double = (x - min) / (max - min)

    override def fromScale(s: Double): Int = (((max - min) * s) + min).asInstanceOf[Int]

    override def toBytes(x: Int): Seq[Byte] = {
      Seq(x, x >> 8).map(_.asInstanceOf[Byte])
    }
  }

  /**
    * Implicit for doubles ranging from -1 to 1
    */
  implicit object PlayableDouble extends Playable[Double] {
    override val max: Double = 1
    override val min: Double = -1

    override def toScale(x: Double): Double = (x + 1) / 2

    override def fromScale(s: Double): Double = (s * 2) - 1
  }
}
