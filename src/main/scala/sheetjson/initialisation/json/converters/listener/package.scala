package sheetjson.initialisation.json.converters

import org.json4s.{DefaultFormats, JObject}
import sheetjson.input.KeyListener
import sheetjson.player.listener.{ActivatableListener, IncrementableListener, Listener, MultiActivatableListener}

package object listener {

  implicit val formats = DefaultFormats

  trait ListenerSetup[ListenerType <: Listener] {
    def setup(listener: ListenerType, json: JObject, keyListener: KeyListener): Unit
  }

  implicit object ActivatableListenerSetup extends ListenerSetup[ActivatableListener] {
    override def setup(listener: ActivatableListener, json: JObject, keyListener: KeyListener): Unit = for {
      key <- (json \ "key").extractOpt[Int]
    } {
      keyListener.listenForPress(key, listener, "activate")
      keyListener.listenForRelease(key, listener, "deactivate")
    }
  }

  implicit object MultiActivatableListenerSetup extends ListenerSetup[MultiActivatableListener] {
    override def setup(listener: MultiActivatableListener, json: JObject, keyListener: KeyListener): Unit = for {
      keys <- (json \ "keys").extractOpt[List[Int]]
      (key, index) <- keys.take(listener.size).zipWithIndex
    } {
      keyListener.listenForPress(key, listener, s"activate($index)")
      keyListener.listenForRelease(key, listener, s"deactivate($index)")
    }
  }

  implicit object IncrementableListenerSetup extends ListenerSetup[IncrementableListener] {
    override def setup(listener: IncrementableListener, json: JObject, keyListener: KeyListener): Unit = {
      val (nextKeyName, previousKeyName) = listener.name match {
        case Some(name) => (s"${name}_next_key", s"${name}_previous_key")
        case None => ("next_key", "previous_key")
      }

      for {
        nextKey <- (json \ nextKeyName).extractOpt[Int]
        previousKey <- (json \ previousKeyName).extractOpt[Int]
      } {
        keyListener.listenForPress(nextKey, listener, "next")
        keyListener.listenForPress(previousKey, listener, "previous")
      }
    }
  }
}