package nivanov.chords.fxgl

import com.almasb.fxgl.dsl.FXGL.getGameTimer
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.BorderPane
import javafx.util.Duration

class ChordPane(node: Node) extends BorderPane(node):
  var prevAction = getGameTimer.runOnceAfter(()=>(), Duration.ZERO)

  def setTransparent = setStyle("-fx-background-color: transparent;")
  def setHightlight = setStyle("-fx-background-color: #97c9f7;-fx-background-radius: 50")

  def hightlightForHalfSecond =
    setHightlight
    prevAction.expire()
    prevAction = getGameTimer.runOnceAfter(() => setTransparent, Duration.seconds(0.5))
