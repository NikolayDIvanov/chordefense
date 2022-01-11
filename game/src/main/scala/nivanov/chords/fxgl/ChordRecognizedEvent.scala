package nivanov.chords.fxgl

import javafx.event.{Event, EventType}
import nivanov.chords.classification.DataSet.Chord

class ChordRecognizedEvent(val chord: Chord) extends Event(ChordRecognizedEvent.anyType)
object ChordRecognizedEvent:
  val anyType = EventType[ChordRecognizedEvent](Event.ANY, "CHORD_RECOGNIZED")
