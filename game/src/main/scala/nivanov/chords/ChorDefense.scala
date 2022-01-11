package nivanov.chords

import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import javafx.application.Application
import com.almasb.fxgl.app.{GameApplication, GameSettings}
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.dsl.FXGL.{addUINode, getEventBus, getGameTimer, getGameWorld, getUIFactoryService, getip, inc, random, run, spawn, texture}
import com.almasb.fxgl.entity.{Entity, SpawnData}
import com.thoughtworks.xstream.XStream
import javafx.geometry.Side.{BOTTOM, LEFT, RIGHT, TOP}
import javafx.geometry.{Insets, Pos, Side}
import javafx.scene.layout.{HBox, VBox}
import javafx.util.Duration
import javafx.util.Duration.seconds
import nivanov.chords.classification.ModelWrapper
import nivanov.chords.classification.DataSet.Chord
import nivanov.chords.common.{Constants, GameState}
import nivanov.chords.audio.{AudioUtil, PcpAudioProcessor}
import nivanov.chords.audio.AudioUtil.Pcp
import nivanov.chords.fxgl.{ChordPane, ChordRecognizedEvent}
import nivanov.chords.entity.{GameEntityFactory, MeteorComponent}
import smile.classification.Classifier

import scala.collection.concurrent.TrieMap

class ChorDefense extends GameApplication:
  val state = GameState()

  override def initSettings(settings: GameSettings) =
    settings.setTitle("ChorDefense")
    settings.setWidth(Constants.windowW)
    settings.setHeight(Constants.windowH)
    settings.setScaleAffectedOnResize(true)
    settings.setPreserveResizeRatio(true)

  override def initGameVars(vars: java.util.Map[String, Any]) =
    vars.put("score", 0)
    vars.put("lives", 5)

  override def initInput: Unit =
    val pcpProcessor = PcpAudioProcessor(AudioUtil.defaultSampleRate, AudioUtil.defaultBufferSize, predictChord)
    val dispatcher = AudioDispatcherFactory
      .fromDefaultMicrophone(AudioUtil.defaultSampleRate.toInt, AudioUtil.defaultBufferSize, AudioUtil.defaultBufferSize / 2)

    dispatcher.addAudioProcessor(pcpProcessor)
    Async.INSTANCE.startAsync(dispatcher)

    getEventBus.addEventHandler(ChordRecognizedEvent.anyType, handleChordRecognizedEvent)

  override def initGame: Unit =
    getGameWorld.addEntityFactory(GameEntityFactory)

    spawn("background")

    val planetX = Constants.chordsPanelW + (Constants.windowW - Constants.chordsPanelW - Constants.planetSize) / 2
    val planetY = (Constants.windowH - Constants.planetSize) / 2
    val planet = spawn("planet", planetX, planetY)

    state.planetCenter = planet.getCenter
    state.planetRadius = planet.getWidth / 2d

    run(() => spawn("meteor", randomizeMeteorSpawnData), seconds(1 + random(0f, 5f) / 100))

  override def initUI =
    addScoreAndLiveLabels
    addChordsPanes

  def addScoreAndLiveLabels =
    val scoreText = getUIFactoryService.newText("", 24)
    val livesText = getUIFactoryService.newText("", 24)
    val padding = 20

    scoreText.textProperty.bind(getip("score").asString("Score: %d"))
    livesText.textProperty.bind(getip("lives").asString("Lives: %d"))

    addUINode(scoreText, Constants.windowW - 100 - padding, 10 + padding)
    addUINode(livesText, Constants.windowW - 100 - padding, (10 + padding) * 2)

  def addChordsPanes =
    val w = (Constants.chordsPanelW / 1.5).toInt
    val h = (Constants.chordsPanelH / (state.chords.size + 0.5)).toInt
    val padding = 20

    state.chords
      .map(c => c -> ChordPane(texture(s"chord_${c.toString.toLowerCase}.png", w, h)))
      .foreach(state.chordPanes.put)

    val panes = state.chords.map(state.chordPanes.get(_).get).map(HBox(_))
    val chordsBox = VBox(panes:_*)

    panes.foreach(_.setAlignment(Pos.CENTER))
    chordsBox.setStyle("-fx-background-color: rgba(255, 255, 228, 0.95);-fx-background-radius: 10;")
    chordsBox.setMinSize(Constants.chordsPanelW, Constants.chordsPanelH)
    chordsBox.setMaxSize(Constants.chordsPanelW, Constants.chordsPanelH)
    chordsBox.setPadding(Insets(padding, padding, padding, padding))
    chordsBox.setAlignment(Pos.CENTER)
    chordsBox.setTranslateX(padding)
    chordsBox.setTranslateY(padding)
    chordsBox.setSpacing(padding)

    addUINode(chordsBox)

  def randomizeMeteorSpawnData =
    val (x, y) = Side.values()(random(0, 3)) match
      case TOP => random(Constants.windowW * 0.15, Constants.windowW) -> -Constants.meteorH.toDouble
      case BOTTOM => random(Constants.windowW * 0.15, Constants.windowW) -> Constants.windowH.toDouble
      case LEFT => Constants.windowW * 0.15 - Constants.meteorW -> random(0d, Constants.windowH)
      case RIGHT => Constants.windowW.toDouble -> random(0d, Constants.windowH)

    val spawnData = SpawnData(x, y)
    spawnData.put("state", state)
    spawnData.put("chord", state.chords(random(0, state.chords.size - 1)))

  def predictChord(pcp: Pcp) =
    val (predictedClass, score) = state.model.predictWithScore(pcp)

    if score > Constants.chordScoreThreshold then
      val chord = Chord.fromOrdinal(predictedClass)
      getGameTimer.runOnceAfter(() => getEventBus.fireEvent(ChordRecognizedEvent(chord)), Duration.ZERO)

    true

  def handleChordRecognizedEvent(e: ChordRecognizedEvent) =
    state.chordPanes.get(e.chord).map(_.hightlightForHalfSecond)
    state.meteors.get(e.chord)
      .filter(_.nonEmpty)
      .map(_.minBy(_.distanceFromPlanet).kill(_ => inc("score", +1)))

object ChorDefense:
  def main(args: Array[String]) = GameApplication.launch(classOf[ChorDefense], args)
