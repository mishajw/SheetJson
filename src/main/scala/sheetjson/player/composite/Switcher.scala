package sheetjson.player.composite

import sheetjson.input.KeyListener.KeyCode
import sheetjson.player.activatable.MultiKeyInteractivePlayer
import sheetjson.player.activatable.MultiKeyInteractivePlayer.MultiKeyInteractiveSpec
import sheetjson.player.{Playable, Player, PlayerSpec}

class Switcher(_wrapped: Seq[Player],
               _spec: PlayerSpec,
               override val interactiveSpec: MultiKeyInteractiveSpec)
    extends CompositePlayer[Player](_spec) with MultiKeyInteractivePlayer {

  override protected val wrapped: Seq[Player] = _wrapped

  override protected def extract(t: Player): Player = t

  private var currentIndex: Option[Int] = None

  override protected def _play: Playable = {
    (for {
      c <- currentIndex
      if wrapped.size > c
      player = wrapped(c)
    } yield player.play).getOrElse(Playable.default)
  }

  override def activate(i: KeyCode): Unit = {}

  override def deactivate(i: KeyCode): Unit = currentIndex = Some(i)
}
