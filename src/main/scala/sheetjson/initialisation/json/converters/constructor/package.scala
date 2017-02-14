package sheetjson.initialisation.json.converters

import org.json4s.JObject
import org.json4s.JsonAST.JString
import sheetjson.initialisation.json.JsonParser
import sheetjson.jsonFailure
import sheetjson.player.Player
import sheetjson.player.composite.{CompositePlayer, Keyboard}
import sheetjson.player.listener.ActivatableListener
import sheetjson.util.Notes.RelativeNote
import sheetjson.util.{Notes, Scales}

import scala.util.{Failure, Success, Try}

package object constructor {

  type ConstructParams = JObject
  type ConstructFunction[ChildType] = (ConstructParams => ChildType)

  trait ConstructorConverter[T <: CompositePlayer[_]] extends JsonConverter[T] {

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

    def applyWithComponents(components: Seq[Player], json: JObject): Try[T] =
      applyWithComponentsOpt(components, json) match {
        case Some(t) => Success(t)
        case None => jsonFailure(s"Couldn't parse JSON for Constructor player $getClass", json)
      }

    def applyWithComponentsOpt(components: Seq[Player], json: JObject): Option[T] = None

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
  implicit object KeyboardConverter extends ConstructorConverter[Keyboard] {
    override def applyWithComponentsOpt(components: Seq[Player], json: JObject): Option[Keyboard] = {
      val listenerComponents = components collect { case p: ActivatableListener => p }
      Some(new Keyboard(listenerComponents, getSpec(json)))
    }

    override def componentParams(json: JObject): Try[Seq[JObject]] = {
      // TODO: Use better declaration of JSON

      val notesOpt = (json \ "notes").extractOpt[Seq[String]]
      val scaleOpt = (json \ "scale").extractOpt[String]
      val keyOpt = (json \ "key").extractOpt[String]

      val notesTry: Try[Seq[String]] = (notesOpt, scaleOpt, keyOpt) match {
        case (Some(notes), None, None) =>
          Success(notes)
        case (Some(Seq()), Some(scale), Some(key)) => Notes.relativeNoteFor(key) flatMap (Scales.get(_, scale)) match {
          case Some(notes: Seq[RelativeNote]) => Success(notes.map(_.str))
          case None => jsonFailure(s"Scale $scale for key $key can't be found", json)
        }
        case _ => jsonFailure("Can't create keyboard without notes, or scale and key", json)
      }

      notesTry match {
        case Success(notes) =>
          Success(notes.map(n => JObject(List("$note" -> JString(n)))))
        case Failure(e) => Failure(e)
      }
    }
  }
}
