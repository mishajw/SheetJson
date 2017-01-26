package sheetjson.management

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
}

object Identifiable {
  def propagateParents(identifiable: Identifiable): Unit = {
    val children: Seq[Identifiable] =
    {
      {
        identifiable match {
          case filter: FilterPlayer =>
            Seq(filter.child)
          case composite: CompositePlayer[_] =>
            composite.components
          case _ => Seq()
        }
      } ++
      {
        identifiable match {
          case listener: ListenerPlayer =>
            listener.listeners
          case _ => Seq()
        }
      }
    }.filter(_ != identifiable)

    children foreach (_.parentOpt = Some(identifiable))
    children foreach propagateParents
  }
}
