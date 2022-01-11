package nivanov.chords.audio

import be.tarsos.dsp.{AudioProcessor, AudioEvent}
import be.tarsos.dsp.util.fft.FFT
import nivanov.chords.audio.PcpCalculator
import nivanov.chords.audio.AudioUtil.Pcp

class PcpAudioProcessor(sampleRate: Float, bufferSize: Int,
                        onPcpCalculated: Pcp => Boolean,
                        onProcessingFinishedOpt: Option[() => Unit] = None) extends AudioProcessor:

  val fft = FFT(bufferSize)

  override def process(audioEvent: AudioEvent) =
    val audioBuffer = audioEvent.getFloatBuffer.clone

    fft.forwardTransform(audioBuffer)

    val pcp = PcpCalculator.compute(audioBuffer, sampleRate)
    onPcpCalculated(pcp)

  def processingFinished() = onProcessingFinishedOpt.map(_())
