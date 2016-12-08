package sheetjson.player.filter

import sheetjson.player.{Playable, Player, PlayerSpec, WaveFunction}
import sheetjson.util.Time.Bars

class VolumeFunction(val waveFunction: WaveFunction,
                     val frequency: Bars,
                     _child: Player,
                     _spec: PlayerSpec) extends FilterPlayer(_child, _spec) {

  override protected def _play: Playable = {
    val currentStep = (Bars(absoluteStep) % frequency) / frequency
    val waveResult = waveFunction(currentStep.toDouble)

    child.play * waveResult
  }
}
