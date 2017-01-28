package sheetjson.management.json.converters

import org.json4s.{DefaultFormats, JObject}
import sheetjson.input.KeyListener
import sheetjson.player.listener.{ActivatableListener, Listener, ListenerPlayer}

package object listener {

  implicit val formats = DefaultFormats

  trait ListenerSetup[ListenerType] {
    def setup(listener: Listener, json: JObject, keyListener: KeyListener): Unit
  }

  implicit object ActivatableListenerSetup extends ListenerSetup[ActivatableListener] {
    override def setup(listener: Listener, json: JObject, keyListener: KeyListener): Unit = for {
      key <- (json \ "key").extractOpt[Int]
    } {
      keyListener.listenForPress(key, listener, "activate")
      keyListener.listenForRelease(key, listener, "deactivate")
    }
  }
}
