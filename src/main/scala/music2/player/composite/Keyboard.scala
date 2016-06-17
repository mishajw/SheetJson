package music2.player.composite

import java.awt.AWTKeyStroke
import java.awt.event.KeyEvent._
import javax.swing.KeyStroke

import music2.management.KeyListener.KeyCode
import music2.player.filter.KeyActivated
import music2.player.origin.Tone
import music2.player.util.Notes.RelativeNote
import music2.player.util.Frequencies.FrequencyOf
import music2.player.{Playable, Player, PlayerSpec}

class Keyboard(playerKeys: Seq[(Player, KeyCode)],
               _spec: PlayerSpec) extends CompositePlayer[Player](_spec) {

  def this(scale: Seq[RelativeNote], spec: PlayerSpec = PlayerSpec())(implicit i: DummyImplicit) =
    this({
      val keys = Seq(VK_A, VK_S, VK_D, VK_F, VK_G, VK_H, VK_J, VK_K, VK_L)
      scale
        .map(_.frequency)
        .map(new Tone(_))
        .zip(keys)
    }, spec)

  override protected val wrapped: Seq[Player] =
    playerKeys map { case (p, k) => new KeyActivated(k, p) }

  override protected def extract(t: Player): Player = t

  override protected def _play: Playable = {

    val (pr, npr) = components
      .map(_.asInstanceOf[KeyActivated])
      .partition(_.pressed)

    npr.foreach(_.play)

    pr.map(_.play) combine
  }
}
