package sheetjson.player.composite

import java.awt.event.KeyEvent._

import sheetjson.player.PlayerSpec
import sheetjson.player.origin.Tone
import sheetjson.util.Frequencies.FrequencyOf
import sheetjson.util.Notes.RelativeNote

class KeyboardScale(scale: Seq[RelativeNote], _spec: PlayerSpec)
  extends Keyboard({
      val keys = Seq(VK_A, VK_S, VK_D, VK_F, VK_G, VK_H, VK_J, VK_K, VK_L)
      scale
        .map(_.frequency)
        .map(new Tone(_, _spec = PlayerSpec(visible = false)))
        .zip(keys)
    }, _spec)
