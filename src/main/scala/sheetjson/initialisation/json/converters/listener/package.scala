package sheetjson.initialisation.json.converters

import com.typesafe.scalalogging.Logger
import org.json4s.{DefaultFormats, JObject}
import sheetjson.input.KeyListener
import sheetjson.player.listener.{ActivatableListener, IncrementableListener, Listener, MultiActivatableListener}
import sheetjson.jsonFailure

package object listener {

  implicit val formats = DefaultFormats

  private val log = Logger(getClass)

  trait ListenerSetup[ListenerType <: Listener] {
    def setup(listener: ListenerType, json: JObject, keyListener: KeyListener): Unit
  }

  implicit object ActivatableListenerSetup extends ListenerSetup[ActivatableListener] {
    override def setup(listener: ActivatableListener, json: JObject, keyListener: KeyListener): Unit = {
      val keyOpt = (json \ "key").extractOpt[Int]
      val toggleOnOpt = (json \ "on_key").extractOpt[Int]
      val toggleOffOpt = (json \ "off_key").extractOpt[Int]

      (keyOpt, toggleOnOpt, toggleOffOpt) match {
        case (Some(key), None, None) =>
          keyListener.listenForPress(key, listener, "activate")
          keyListener.listenForRelease(key, listener, "deactivate")
        case (None, Some(toggleOn), Some(toggleOff)) =>
          keyListener.listenForPress(toggleOn, listener, "activate")
          keyListener.listenForPress(toggleOff, listener, "deactivate")
        case (None, None, None) =>
          // Accepted because parent might activate
        case _ =>
          log.error(
            "JSON error when parsing listener",
            jsonFailure(
              "Must specify either `key` or `on_key` and `off_key` for an activatable listener",
              json))
      }
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
