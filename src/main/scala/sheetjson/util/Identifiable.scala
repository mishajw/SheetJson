package sheetjson.util

import sheetjson.player.composite.CompositePlayer
import sheetjson.player.filter.FilterPlayer
import sheetjson.player.listener.ListenerPlayer

trait Identifiable {
  var parentOpt: Option[Identifiable] = None

  def path: Seq[String] = parentOpt match {
    case Some(parent) => parent.path :+ parent.toString
    case None => Seq()
  }

  def identifier = (path :+ toString).mkString("/")

  def identifiableChildren: Seq[Identifiable] = Seq()

  def propagateParents(): Unit = {
    val children = identifiableChildren.filter(_ != this)
    children foreach (_.parentOpt = Some(this))
    children foreach (_.propagateParents())
  }
}
