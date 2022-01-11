package nivanov.chords.entity

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.dsl.FXGL.{inc, spawn}
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component
import javafx.geometry.Point2D
import nivanov.chords.classification.DataSet.Chord
import nivanov.chords.common.GameState

import scala.collection.mutable.Map

class MeteorComponent(chord: Chord, state: GameState) extends Component:
  var directionAngle = 0D

  override def onUpdate(tpf: Double) =
    val dir = Vec2.fromAngle(directionAngle).mulLocal(0.6)

    entity.setRotation(directionAngle - 90)
    entity.translate(dir)

    collisionHandling

  override def onAdded =
    state.meteors.updateWith(chord) {
      case Some(xs) => Some(this :: xs)
      case None => Some(this :: Nil)
    }
    val dy = state.planetCenter.getY - entity.getCenter.getY
    val dx = state.planetCenter.getX - entity.getCenter.getX
    directionAngle = Math.toDegrees(Math.atan2(dy, dx))

  override def onRemoved: Unit = state.meteors.updateWith(chord) {
      case Some(xs) => Some(xs.filter(_ != this))
      case _ => None
    }

  def distanceFromPlanet = state.planetCenter.distance(entity.getCenter)

  def collisionHandling =
    val isCollidingWithPlanet = distanceFromPlanet <= entity.getHeight / 2 + state.planetRadius
    if isCollidingWithPlanet then kill(_ => inc("lives", -1))

  def kill(onKilled: Entity => Unit) =
    val explosionSpawnPoint = entity.getPosition
      .add(Vec2.fromAngle(directionAngle).mulLocal(entity.getHeight / 2).toPoint2D)

    spawn("explosion", explosionSpawnPoint)
    entity.removeFromWorld()

    onKilled(entity)
