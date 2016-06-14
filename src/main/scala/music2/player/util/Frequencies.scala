package music2.player.util

import Notes._
import music2.Frequency
import music2.player.SimpleTone

object Frequencies {

  /**
    * A map of notes to frequencies, using the base octave
    */
  val frequencyMap = Map[RelativeNote, Frequency](
    C -> 261.626, Cs -> 277.183,
    D -> 293.665, Ds -> 311.127,
    E -> 329.628,
    F -> 349.228, Fs -> 369.994,
    G -> 391.995, Gs -> 415.305,
    A -> 440.000, As -> 446.164,
    B -> 493.8831
  )

  /**
    * @param note the note to get
    * @return a SimpleTone object with the correct frequency
    */
  def get(note: Note): SimpleTone = note match {
    case rn: RelativeNote =>
      new SimpleTone(getFrequency(AbsoluteNote(rn, defaultOctave)))
    case an: AbsoluteNote =>
      new SimpleTone(getFrequency(an))
  }

  /**
    * @param on the octave note to get
    * @return the frequency of this note and octave
    */
  def getFrequency(on: AbsoluteNote): Frequency = {
    frequencyMap get on.note match {
      case None =>
        throw new IllegalArgumentException(s"Couldn't find frequency for $on")
      case Some(f) =>
        val relativeOctave = on.octave - defaultOctave
        f * (2 ^ relativeOctave)
    }
  }
}
