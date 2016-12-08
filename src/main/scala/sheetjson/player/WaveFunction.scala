package sheetjson.player

import sheetjson.player.WaveFunction.CoreWaveFunction

class WaveFunction(coreFunction: CoreWaveFunction) {
  def apply(x: Double): Double = {
    coreFunction(x)
  }

  lazy val limitedUnsigned =
    new WaveFunction(x => Math.min(0, Math.max(1, coreFunction(x))))

  lazy val limitedSigned =
    new WaveFunction(x => limitedUnsigned(x) * 2 - 1)
}

object WaveFunction {

  type CoreWaveFunction = Double => Double

  private val fullAngle = 2 * Math.PI

  private lazy val coreFunctions: Map[String, CoreWaveFunction] = Map(
    "sine" -> { x: Double => Math.sin(x * fullAngle) },
    "cosine" -> { x: Double => Math.cos(x * fullAngle) },
    "zigzag" -> { x: Double =>
      if (x < 0.5) {
        (x * 4) - 1
      } else {
        (1 - ((x - 0.5) * 2)) * 2 - 1
      }
    },
    "id" -> { x: Double => x },
    "binary" -> { x: Double => if (x < 0.5) -1 else 1 }
  )

  private lazy val waveFunctions =
    coreFunctions.map {
      case (name, function) => (name, new WaveFunction(function))
    }

  def get(name: String): WaveFunction =
      waveFunctions(name)

  def getOpt(name: String): Option[WaveFunction] =
    if (waveFunctions contains name)
      Some(waveFunctions(name))
    else
      None
}
