package sheetjson.player.origin

import sheetjson.player.{Player, PlayerSpec}

abstract class OriginPlayer(_spec: PlayerSpec) extends Player(_spec) {
  /**
    * Set to 0.5, because while filter/composite players need to not quieten children, the children shouldn't all be
    * 100% volume otherwise distortion will occur
    */
  override val defaultVolume: Double = 0.5
}
