package nivanov.chords.common

import com.thoughtworks.xstream.XStream
import javafx.geometry.Point2D
import nivanov.chords.audio.AudioUtil.Pcp
import nivanov.chords.classification.DataSet.Chord
import nivanov.chords.classification.ModelWrapper
import nivanov.chords.entity.MeteorComponent
import nivanov.chords.fxgl.ChordPane
import smile.classification.Classifier

import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArraySeq
import scala.util.Random

class GameState:
  lazy val model =
    val model = XStream().fromXML(getClass.getClassLoader.getResource(Constants.modelPath))
    ModelWrapper(model.asInstanceOf[Classifier[Pcp]])

  val meteors = TrieMap.empty[Chord, List[MeteorComponent]]
  val chords = Constants.chordGroups(0) //Random.shuffle(ArraySeq.from(Chord.values)).take(6)
  val chordPanes = TrieMap.empty[Chord, ChordPane]

  var planetCenter = Point2D.ZERO
  var planetRadius = Constants.planetSize / 2d
