package music2

package object player {
  case class PlayerSpec(volume: Double = 1, lifeTime: LifeTime = None, speed: Double = 1)
}
