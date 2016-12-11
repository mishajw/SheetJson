package sheetjson.player.filter

import java.util.concurrent.atomic.AtomicBoolean

import com.typesafe.scalalogging.Logger
import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.{ListenerPlayer, Playable, Player, PlayerSpec}

class KeyActivated( _key: KeyCode,
                    _child: Player,
                    _spec: PlayerSpec)
                    extends FilterPlayer(_child, _spec) with ListenerPlayer {

  private val log = Logger(getClass)

  private val _pressed = new AtomicBoolean(false)

  def pressed = _pressed.get()

  override val keys: Seq[KeyCode] = Seq(_key)

  override protected def _play: Playable = {
    val played = child.play

    if (_pressed.get()) played
    else               Playable.default
  }

  override def keyPressed(kc: KeyCode): Unit = {
    _pressed set true
  }

  override def keyReleased(kc: KeyCode): Unit = {
    _pressed set false
  }
}
