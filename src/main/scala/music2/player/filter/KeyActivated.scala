package music2.player.filter

import music2.management.KeyListener.KeyCode
import music2.player.{ListenerPlayer, Playable, Player, PlayerSpec}

class KeyActivated( _key: KeyCode,
                    _child: Player,
                    _spec: PlayerSpec = PlayerSpec())
                    extends FilterPlayer(_child, _spec) with ListenerPlayer {

  private var pressed = false

  override val keys: Seq[KeyCode] = Seq(_key)

  override protected def _play: Playable = {
    if (pressed) child.play
    else         Playable.default
  }

  override def keyPressed(kc: KeyCode): Unit =
    pressed = true

  override def keyReleased(kc: KeyCode): Unit =
    pressed = false
}
