package music2.player.composite

import java.awt.event.KeyEvent._

import music2.player.PlayerSpec
import music2.player.origin.Tone
import music2.util.Frequencies.FrequencyOf
import music2.util.Notes.RelativeNote

class KeyboardScale(scale: Seq[RelativeNote], _spec: PlayerSpec)
  extends Keyboard({
      val keys = Seq(VK_A, VK_S, VK_D, VK_F, VK_G, VK_H, VK_J, VK_K, VK_L)
      scale
        .map(_.frequency)
        .map(new Tone(_, _spec = PlayerSpec()))
        .zip(keys)
    }, _spec)
