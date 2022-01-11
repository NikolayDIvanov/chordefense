package nivanov.chords

import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import nivanov.chords.audio.AudioUtil
import nivanov.chords.classification.DataSet.Chord
import nivanov.chords.classification.{DataSet, ModelWrapper, ModelsUtil}
import nivanov.chords.audio.AudioUtil.Pcp
import nivanov.chords.audio.PcpAudioProcessor
import nivanov.chords.common.Config
import smile.classification.{SVM, knn, ovr}
import smile.math.kernel.GaussianKernel

@main def train =
  ModelsUtil.trainModel("SVM", Config.svmModelPath) {
    ovr(_,_) { SVM.fit(_,_, GaussianKernel(0.3), 20, 1E-4) }
  }
  ModelsUtil.trainModel("KNN", Config.knnModelPath) { knn(_,_, 12) }

@main def realTest =
  val svm = ModelWrapper(ModelsUtil.load(Config.svmModelPath))

  val pcpProcessor = PcpAudioProcessor(AudioUtil.defaultSampleRate, AudioUtil.defaultBufferSize, onPcp(svm,_))
  val dispatcher = AudioDispatcherFactory
    .fromDefaultMicrophone(AudioUtil.defaultSampleRate.toInt, AudioUtil.defaultBufferSize, AudioUtil.defaultBufferSize / 2)

  dispatcher.addAudioProcessor(pcpProcessor)
  Thread(dispatcher).start()

def onPcp(model: ModelWrapper[Pcp], pcp: Pcp) =
  val (predictedClass, probability) = model.predictWithProbability(pcp)
  val chord = Chord.fromOrdinal(predictedClass)

  print(s"Predicted chord: ${Chord.fromOrdinal(predictedClass)}, probability: $probability, ")
  println(s"Pcp: [${pcp.mkString(",")}]")
  true
