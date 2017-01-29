package sheetjson.util

import sheetjson.player.{Playable, Player}
import sheetjson.util.Time.{Absolute, Bars, Seconds}

import scala.collection.mutable.ArrayBuffer

class PlayerCache(player: Player) {

  private val played = ArrayBuffer[Playable]()

  def play(index: Int): Playable = {
    while (played.size <= index)
      played += player.play

    played(index)
  }

  def play(absolute: Absolute): Playable = play(absolute.toInt)

  def play(bars: Bars): Playable = play(Absolute(bars))

  def play(seconds: Seconds): Playable = play(Absolute(seconds))
}
