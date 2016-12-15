package sheetjson.player
import sheetjson.management.KeyListener.KeyCode

trait ActivatablePlayer extends ListenerPlayer {

  protected var _isActive = false

  def isActive = _isActive

  override def keyPressed(kc: KeyCode): Unit = {
    _isActive = true
    activate()
  }

  override def keyReleased(kc: KeyCode): Unit = {
    _isActive = false
    deactivate()
  }

  def activate()

  def deactivate()
}

/**
  *
  * Plan for ActivatablePlayers
  *  - ActivatableSpec
  *    - Can have key, keys, or nothing
  *    - Possibly future things like mouse and everything?
  *  - Different types of ActivatablePlayer
  *    - Normal
  *      - activate(), deactivate()
  *    - Single key
  *      - As above, but has to have a key associated with it
  *    - Indexed
  *      - activate(i), deactivate(i), where i is the index of the key pressed
  *    - Full (needs better name)
  *      - activate(kc), deactivate(kc), where kc is the KeyCode of the key pressed
  *  - ActivatableSpec can have nothing, with defaults:
  *    - No key: use space
  *    - No keys: use home row
  *
  */
