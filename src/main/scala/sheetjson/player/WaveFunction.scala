package sheetjson.player

import sheetjson.player.WaveFunction.CoreWaveFunction

class WaveFunction(coreFunction: CoreWaveFunction,
                   maxInput: Double = 0,
                   minInput: Double = 1,
                   maxOutput: Double = 0,
                   minOutput: Double = 1) {

  def apply(x: Double): Double = {
    val scaledInput: Double = scale(x, minInput, maxInput)
    val output: Double = coreFunction(scaledInput)
    scale(output, minOutput, maxOutput)
  }

  lazy val signed =
    new WaveFunction(
      x => this(x) * 2 - 1,
      minInput = minInput,
      maxInput = maxInput,
      minOutput = -1,
      maxOutput = 1)

  private def scale(x: Double, max: Double, min: Double): Double =
    (x - min) / (max - min)
}

object WaveFunction {

  type CoreWaveFunction = Double => Double

  private val fullAngle = 2 * Math.PI

  private lazy val waveFunctions: Map[String, WaveFunction] = Map(
    "sine" -> new WaveFunction({ x: Double => Math.sin(x * fullAngle) }, minOutput = -1, maxOutput = 1),
    "cosine" -> new WaveFunction({ x: Double => Math.cos(x * fullAngle) }, minOutput = -1, maxOutput = 1),
    "zigzag" -> new WaveFunction({ x: Double =>
      if (x < 0.5) {
        (x * 4) - 1
      } else {
        (1 - ((x - 0.5) * 2)) * 2 - 1
      }
    }),
    "id" -> new WaveFunction({ x: Double => x }),
    "binary" -> new WaveFunction({ x: Double => if (x < 0.5) 0 else 1 })
  )

  def get(name: String): WaveFunction =
      waveFunctions(name)

  def getOpt(name: String): Option[WaveFunction] =
    if (waveFunctions contains name)
      Some(waveFunctions(name))
    else
      None
}
