package music2.player.util

import Notes._
import music2.Frequency
import music2.player.SimpleTone

object Frequencies {

  /**
    * The default octave
    */
  private val defaultOctave = 4

  /**
    * A map of notes to frequencies, using the base octave
    */
  val frequencyMap = Map[Note, Frequency](
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
    * @param octave the octave of the note (default is 4)
    * @return a SimpleTone object with the correct frequency
    */
  def get(note: Note, octave: Int = defaultOctave): SimpleTone = {
    new SimpleTone(getFrequency(note, octave))
  }

  /**
    * @param note the note to get
    * @param octave the octave of the note (default is 4)
    * @return the frequency of this note and octave
    */
  def getFrequency(note: Note, octave: Int = defaultOctave): Frequency = {
    frequencyMap get note match {
      case None =>
        throw new IllegalArgumentException(s"Couldn't find frequency for $note")
      case Some(f) =>
        val relativeOctave = octave - defaultOctave
        f * (2 ^ relativeOctave)
    }
  }
}
