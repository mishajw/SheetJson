package sheetjson.management.json.converters

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
      if keys.size == listener.size
      (key, index) <- keys.zipWithIndex
    } {
      keyListener.listenForPress(key, listener, s"activate($index)")
      keyListener.listenForRelease(key, listener, s"deactivate($index)")
    }
  }

  implicit object IncrementableListenerSetup extends ListenerSetup[IncrementableListener] {
    override def setup(listener: IncrementableListener, json: JObject, keyListener: KeyListener): Unit = for {
      nextKey <- (json \ "next_key").extractOpt[Int]
      previousKey <- (json \ "previous_key").extractOpt[Int]
    } {
      keyListener.listenForPress(nextKey, listener, "next")
      keyListener.listenForPress(previousKey, listener, "previous")
    }
  }
}
