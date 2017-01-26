package sheetjson.player.filter

import sheetjson.player.{Player, PlayerSpec}

abstract class FilterPlayer(val child: Player, _spec: PlayerSpec) extends Player(_spec) {
  override def childrenAlive: Boolean = child.alive

  override def propagateParents(): Unit = {
    child.parentOpt = Some(this)
    child.propagateParents()
  }
}
