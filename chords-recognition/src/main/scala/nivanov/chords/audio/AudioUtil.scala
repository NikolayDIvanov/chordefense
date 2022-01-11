package nivanov.chords.audio

import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import nivanov.chords.audio.PcpAudioProcessor

import java.nio.file.Path
import java.util.ArrayList
import javax.sound.sampled.AudioSystem
import collection.convert.ImplicitConversions.*

object AudioUtil:
  type Pcp = Array[Double]

  val defaultSampleRate = 44100f
  val defaultBufferSize = 1024 * 18

  def computePcpFrames(path: Path, sampleRate: Float = defaultSampleRate, bufferSize: Int = defaultBufferSize) =
    val framedFeatures = ArrayList[Pcp]()
    val dispatcher = AudioDispatcherFactory.fromFile(path.toFile, bufferSize, bufferSize / 2)

    dispatcher.addAudioProcessor(PcpAudioProcessor(sampleRate, bufferSize, arr => framedFeatures.add(arr)))
    dispatcher.run

    framedFeatures.toList

  def getAudioPlaybackMixers = AudioSystem.getMixerInfo
    .map(AudioSystem.getMixer)
    .filter(_.getSourceLineInfo.size != 0)
