package music2.management.json

import music2.player._
import music2.player.composite.{Combiner, Riff}
import music2.player.composite.Riff.{PlayerDuration, PlayerSpan}
import music2.player.util.Frequencies.FrequencyOf
import music2.player.util.Notes
import org.json4s.DefaultFormats
import org.json4s.JsonAST._

object JsonImplicits {

  implicit val formats = DefaultFormats

  def fromJson(json: JObject): Option[Player] = for {
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

  private def getComponents(json: JObject): Seq[Player] = for {
    JObject(obj) <- json
    ("components", JArray(components)) <- obj
    jsonComponent @ JObject(_) <- components
    component <- fromJson(jsonComponent)
  } yield component

  case class JTone(note: Double, waveFunction: String = "sine")

  type JsonConverter = JObject => Option[Player]

  val converters: Map[String, JsonConverter] = Map(

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

      val waveFunctionNames = Map(
        "sine" -> Tone.sine,
        "cosine" -> Tone.cosine,
        "id" -> Tone.id
      )

      transformed.extractOpt[JTone]
        .map(jTone => new Tone(jTone.note, waveFunctionNames(jTone.waveFunction), getSpec(json)))
    },

    "combiner" -> { json: JObject =>
      Some(new Combiner(
        getComponents(json),
        getSpec(json)))
    },

    "riff" -> { json: JObject =>
      val descriptions = for {
        JObject(obj) <- json
        ("components", JArray(components)) <- obj
        JArray((jsonComponent: JObject) :: description) <- components
      } yield (fromJson(jsonComponent), description) match {
        case (Some(player), List(JDouble(start), JDouble(end))) =>
          PlayerSpan(player, start, end)
        case (Some(player), List(JDouble(duration))) =>
          PlayerDuration(player, duration)
      }

      Some(new Riff(descriptions, getSpec(json)))
    }
  )
}
