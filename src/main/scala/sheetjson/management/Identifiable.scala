package sheetjson.management

trait Identifiable {
  var parentOpt: Option[Identifiable] = None

  def path: Seq[String] = parentOpt match {
    case Some(parent) => parent.path :+ parent.toString
    case None => Seq()
  }

  def identifier = (path :+ toString).mkString("/")

  def propagateParents(): Unit
}
