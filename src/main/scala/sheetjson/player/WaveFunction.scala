package sheetjson.player

import sheetjson.player.WaveFunction.CoreWaveFunction

class WaveFunction(coreFunction: CoreWaveFunction,
                   maxInput: Double = 0,
                   minInput: Double = 0,
                   maxOutput: Double = 1,
                   minOutput: Double = 1) {

  def apply(x: Double): Double = {
    val scaledInput: Double = scale(x, minInput, maxInput)
    val output: Double = coreFunction(scaledInput)
    scale(output, minOutput, maxOutput)
  }

  lazy val limitedSigned =
    new WaveFunction(x => this(x) * 2 - 1, minOutput = -1, maxOutput = 1)

  private def scale(x: Double, max: Double, min: Double): Double =
    (x - min) / (max - min)
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
