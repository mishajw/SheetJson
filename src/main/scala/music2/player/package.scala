package music2

package object player {
  case class PlayerSpec(
    volume: Option[Double] = None,
    lifeTime: Option[Double] = None,
    speed: Option[Double] = None)
}
