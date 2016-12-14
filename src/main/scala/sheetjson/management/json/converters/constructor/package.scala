package sheetjson.management.json.converters

import org.json4s.JsonAST.{JInt, JString}
import org.json4s.{JObject, JValue}
import sheetjson.player.composite.{CompositePlayer, Keyboard}
import sheetjson.jsonFailure
import sheetjson.management.KeyListener.KeyCode
import sheetjson.management.json.JsonParser
import sheetjson.player.Player
import sheetjson.util.Notes

import scala.util.{Failure, Success, Try}

package object constructor {

  type ConstructParams = JObject
  type ConstructFunction[ChildType] = (ConstructParams => ChildType)

  trait ConstructorConverter[T <: CompositePlayer[_], V] extends JsonConverter[T] {

    // TODO: Must be a better way to do this...
    def applyParams(presetName: String, params: Seq[JObject]): Try[Seq[Player]] = {
      val components: Seq[Try[Player]] = for (p <- params) yield {
        JsonParser.getPlayer(presetName, p)
      }

      val (success, failure) = components partition {
        case Success(_) => true
        case Failure(_) => false
      }

      failure match {
        case Failure(e) :: _ => Failure(e)
        case Nil => Success(success collect {
          case Success(player) => player
        })
      }
    }

    override def apply(json: JObject): Try[T] = {
      val componentsOpt: Try[Seq[Player]] =
        ((json \ "preset").extractOpt[String], componentParams(json)) match {
          case (Some(presetName), Success(params)) =>
            applyParams(presetName, params)
          case (None, _) =>
            jsonFailure("Couldn't find preset name", json)
          case (_, Failure(e)) =>
            Failure(e)
        }

      componentsOpt match {
        case Success(components) =>
          applyWithComponents(components, json)
        case Failure(e) => Failure(e)
      }
    }

    def applyWithComponents(components: Seq[Player], json: JObject): Try[T]

    def componentParams(json: JObject): Try[Seq[JObject]]

    def getComponent(json: JObject): Try[Player] = {
      (json \ "preset").extractOpt[String] match {
        case Some(presetName) =>
          JsonParser.getPlayer(presetName, json)
        case None =>
          jsonFailure("Couldn't find preset name", json)
      }
    }
  }

  /**
    * Convert to `Keyboard`
    */
  implicit object KeyboardConverter extends ConstructorConverter[Keyboard, (Player, KeyCode)] {
    override def applyWithComponents(components: Seq[Player], json: JObject): Try[Keyboard] = {
      for {
        keys <- extractTry[Seq[Int]](json, "keys")
        if keys.size == components.size
        keyedComponents = components zip keys
      } yield new Keyboard(keyedComponents, getSpec(json))
    }

    override def componentParams(json: JObject): Try[Seq[JObject]] = {
      // TODO: Use better declaration of JSON
      for (notes <- extractTry[Seq[String]](json, "notes")) yield
        notes.map(note => JObject(List("$note" -> JString(note))))
    }
  }
}
