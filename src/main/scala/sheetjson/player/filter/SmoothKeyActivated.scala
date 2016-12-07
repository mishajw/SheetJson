package sheetjson.player.filter

import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.origin.Tone.WaveFunction
import sheetjson.player.{ListenerPlayer, Playable, Player, PlayerSpec}
import sheetjson.util.Time.Absolute

import scala.collection.mutable.ArrayBuffer

class SmoothKeyActivated(keyCode: Int,
                         val inFunction: WaveFunction,
                         val outFunction: WaveFunction,
                         _child: Player,
                         _spec: PlayerSpec) extends FilterPlayer(_child, _spec) with ListenerPlayer {

  override val keys: Seq[KeyCode] = Seq(keyCode)

  val childPlays: ArrayBuffer[Playable] = ArrayBuffer()

  var lastPressed: Option[Absolute] = None

  override protected def _play: Playable = lastPressed match {
    case Some(absolute) => getChildPlayed(absoluteStep - absolute)
    case None => Playable.default
  }

  def getChildPlayed(absolute: Absolute): Playable = {
    while (absolute.toInt >= childPlays.length)
      childPlays += child.play

    childPlays(absolute.toInt)
  }

  override def keyPressed(kc: KeyCode): Unit = {
    lastPressed = Some(absoluteStep)
  }

  override def keyReleased(kc: KeyCode): Unit = {
    lastPressed = None
  }
}
