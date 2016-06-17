package music2.player.composite

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
      val keys = Seq('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l')
      scale
        .map(_.frequency)
        .map(new Tone(_))
        .zip(
          keys map {
            KeyStroke
              .getKeyStroke(_, 0)
              .getKeyCode
          }
        )
    }, spec)

  override protected val wrapped: Seq[Player] =
    playerKeys map { case (p, k) => new KeyActivated(k, p) }

  override protected def extract(t: Player): Player = t

  override protected def _play: Playable = components.map(_.play) average
}
