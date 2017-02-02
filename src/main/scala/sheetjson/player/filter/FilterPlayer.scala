package sheetjson.player.filter

import sheetjson.player.{Player, PlayerSpec}
import sheetjson.util.Identifiable

abstract class FilterPlayer(val child: Player, _spec: PlayerSpec) extends Player(_spec) {
  override def childrenAlive: Boolean = child.alive

  override def identifiableChildren: Seq[Identifiable] = super.identifiableChildren :+ child
}
