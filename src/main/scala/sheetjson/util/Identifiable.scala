package sheetjson.util

trait Identifiable {
  var parentOpt: Option[Identifiable] = None

  def path: Seq[String] = parentOpt match {
    case Some(parent) => parent.path :+ parent.toString
    case None => Seq()
  }

  def identifier = (path :+ toString).mkString("/")

  def identifiableChildren: Seq[Identifiable] = Seq()

  def uniqueIdentifiableChildren = identifiableChildren.filter(_ != this)

  def flatten: Set[Identifiable] = {
    val children = uniqueIdentifiableChildren
    (identifiableChildren ++ children.flatMap(_.flatten)).toSet
  }

  def propagateParents(): Unit = {
    val children = uniqueIdentifiableChildren
    children foreach (_.parentOpt = Some(this))
    children foreach (_.propagateParents())
  }
}
