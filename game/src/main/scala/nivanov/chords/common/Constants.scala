package nivanov.chords.common

import nivanov.chords.classification.DataSet.Chord._
import nivanov.chords.entity.MeteorComponent
import nivanov.chords.fxgl.ChordPane

import scala.collection.mutable.{ArraySeq, Map}

object Constants:
  val modelPath = "model/svm.xml"
  val chordScoreThreshold = 0.5

  val windowW = 1600
  val windowH = 900

  val chordsPanelW = windowW * 0.15
  val chordsPanelH = windowH - 50

  val meteorW = 115
  val meteorH = 200

  val planetSize = 250

  val chordGroups = List(
    G :: C :: D :: Em :: Nil,
    C :: F :: G :: Am :: Nil,
    D :: G :: A :: Bm :: Nil,
    Am :: G :: F :: Nil,
    Dm :: G :: C :: Nil
  )
