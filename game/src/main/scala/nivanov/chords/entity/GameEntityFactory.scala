package nivanov.chords.entity

import com.almasb.fxgl.dsl.FXGL.{entityBuilder, texture}
import com.almasb.fxgl.dsl.components.{ExpireCleanComponent, OffscreenCleanComponent}
import com.almasb.fxgl.entity.{EntityFactory, SpawnData, Spawns}
import com.almasb.fxgl.particle.{ParticleComponent, ParticleEmitters}
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.util.Duration.seconds
import nivanov.chords.common.Constants
import nivanov.chords.entity.MeteorComponent

object GameEntityFactory extends EntityFactory:

  @Spawns("background")
  def newBackground(data: SpawnData) = entityBuilder(data)
    .view(texture("background.jpg"))
    .build

  @Spawns("planet")
  def newPlanet(data: SpawnData) = entityBuilder(data)
    .viewWithBBox(texture("planet.png"))
    .collidable
    .build

  @Spawns("meteor")
  def newMeteor(data: SpawnData) =
    val assetName = s"meteor_${data.get("chord").toString.toLowerCase}.png"
    val loadedTexture = texture(assetName, Constants.meteorW * 11, Constants.meteorH)

    entityBuilder(data)
      .viewWithBBox(loadedTexture.toAnimatedTexture(11, seconds(0.8)).loop)
      .`with`(MeteorComponent(data.get("chord"), data.get("state")))
      .`with`(OffscreenCleanComponent())
      .collidable
      .build

  @Spawns("explosion")
  def newExplosion(data: SpawnData) = entityBuilder(data)
      .view(texture("explosion.png").toAnimatedTexture(16, seconds(0.66)).play)
      .`with`(ExpireCleanComponent(seconds(0.66)))
      .`with`(ParticleComponent(newExplosionEmitter))
      .build

  def newExplosionEmitter =
    val emitter = ParticleEmitters.newExplosionEmitter(350)
    emitter.setMaxEmissions(1)
    emitter.setSize(2, 10)
    emitter.setStartColor(Color.WHITE)
    emitter.setEndColor(Color.BLUE)
    emitter.setSpawnPointFunction(_ => Point2D(64, 64))
    emitter
