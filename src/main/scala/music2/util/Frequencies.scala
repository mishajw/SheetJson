package music2.util

import music2.Frequency
import music2.player.PlayerSpec
import music2.player.origin.Tone
import music2.util.Notes._

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
  def get(note: Note): Tone =
    new Tone(getFrequency(note), _spec = PlayerSpec())

  /**
    * @param n the note to get
    * @return the frequency of this note and octave
    */
  def getFrequency(n: Note): Frequency = {
    val an = n match {
      case rn: RelativeNote => AbsoluteNote(rn, defaultOctave)
      case an: AbsoluteNote => an
    }

    frequencyMap get an.note match {
      case None =>
        throw new IllegalArgumentException(s"Couldn't find frequency for $an")
      case Some(f) =>
        val relativeOctave = an.octave - defaultOctave
        f * Math.pow(2, relativeOctave)
    }
  }

  /**
    * Implicit class for easier access of frequencies from `Note`s
    */
  implicit class FrequencyOf(val n: Note) extends AnyVal {

    /**
      * @return the tone of a note
      */
    def tone: Tone = get(n)

    /**
      * @param octave the octave of the note
      * @return the tone of the not at some octave
      */
    def toneOf(octave: Int): Tone = new Tone(frequencyOf(octave), _spec = PlayerSpec())

    def frequency: Frequency = getFrequency(n)

    def frequencyOf(octave: Int): Frequency = n match {
      case note: RelativeNote =>
        getFrequency(AbsoluteNote(note, octave))
      case AbsoluteNote(note, _) =>
        getFrequency(AbsoluteNote(note, octave))
    }
  }
}
