package sheetjson.player.origin

import sheetjson.player.{Player, PlayerSpec}
import sheetjson.util.Identifiable

abstract class OriginPlayer(_spec: PlayerSpec) extends Player(_spec) {
  /**
    * Set to less than 1, because while filter/composite players need to not quieten children, the children shouldn't all be
    * 100% volume otherwise distortion will occur
    */
  override val defaultVolume: Double = 0.2

  override def identifiableChildren: Seq[Identifiable] = super.identifiableChildren
}
