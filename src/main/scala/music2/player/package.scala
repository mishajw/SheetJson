package music2

import music2.util.Time.Bars

package object player {
  case class PlayerSpec(
    volume: Option[Double] = None,
    lifeTime: Option[Bars] = None,
    speed: Option[Double] = None)
}
