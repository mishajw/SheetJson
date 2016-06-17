package music2.player.composite

import java.awt.event.KeyEvent._

import music2.management.KeyListener.KeyCode
import music2.player.filter.KeyActivated
import music2.player.origin.Tone
import music2.player.util.Frequencies.FrequencyOf
import music2.player.util.Notes.RelativeNote
import music2.player.{Playable, Player, PlayerSpec}

class Keyboard(_wrapped: Seq[Player],
               _spec: PlayerSpec) extends CompositePlayer[Player](_spec) {

  override protected val wrapped: Seq[Player] = _wrapped

  override protected def extract(t: Player): Player = t

  override protected def _play: Playable = {
    val (pr, npr) = components
      .map(_.asInstanceOf[KeyActivated])
      .partition(_.pressed)

    npr.foreach(_.play)

    pr.map(_.play) combine
  }
}

object Keyboard {
  def fromKeys(playerKeys: Seq[(Player, KeyCode)], _spec: PlayerSpec) =
    new Keyboard(playerKeys map { case (p, k) => new KeyActivated(k, p) }, _spec)

  def fromScale(scale: Seq[RelativeNote], spec: PlayerSpec = PlayerSpec()) =
    fromKeys({
      val keys = Seq(VK_A, VK_S, VK_D, VK_F, VK_G, VK_H, VK_J, VK_K, VK_L)
      scale
        .map(_.frequency)
        .map(new Tone(_))
        .zip(keys)
    }, spec)
}
