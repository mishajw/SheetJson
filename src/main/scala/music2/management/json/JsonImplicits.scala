package music2.management.json

import music2.player.util.Frequencies.FrequencyOf
import music2.player.util.Notes
import music2.player.{Combiner, Player, PlayerSpec, Tone}
import org.json4s.DefaultFormats
import org.json4s.JsonAST._

object JsonImplicits {

  implicit val formats = DefaultFormats

  def toJson(json: JObject): Option[Player] = for {
    t <- getType(json)
    func <- converters get t
    player <- func(json)
  } yield player

  private def getType(json: JObject): Option[String] = {
    json.obj collect {
      case ("type", JString(t)) => t
    } headOption
  }

  private def getSpec(json: JObject): PlayerSpec = {
    json.extract[PlayerSpec]
  }

  case class JTone(note: Double)

  type JsonConverter[T <: Player] = JObject => Option[T]

  val converters = Map(

    "tone" -> { json: JObject =>
      // Transform note strings into frequencies
      // TODO: Handle octaves
      val transformed = json transformField {
        case ("note", JString(n)) =>
          Notes noteFor n match {
            case Some(note) => ("note", JDouble(note frequency))
            case None => "note" -> JString(n)
          }
      }

      transformed.extractOpt[JTone]
        .map(jTone => new Tone(jTone.note, getSpec(json)))
    },

    "combiner" -> { json: JObject =>
      ???
    }
  )
}
