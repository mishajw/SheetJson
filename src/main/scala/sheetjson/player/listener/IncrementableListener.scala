package sheetjson.player.listener

trait IncrementableListener extends Listener {
  override def receive(message: String): Unit = message match {
    case "next" => next()
    case "previous" => previous()
  }

  def next()

  def previous()
}
