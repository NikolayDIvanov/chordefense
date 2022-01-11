package nivanov.chords.classification

import org.slf4j.LoggerFactory
import nivanov.chords.classification.DataSet
import nivanov.chords.audio.AudioUtil.Pcp
import smile.classification.Classifier
import smile.validation.metric.Accuracy

import java.nio.file.Paths

object ModelsUtil:
  val logger = LoggerFactory.getLogger(getClass)

  def load(path: String) = smile.read.xstream(path).asInstanceOf[Classifier[Pcp]]
  def save(model: Classifier[Pcp], path: String) = smile.write.xstream(model, path)

  def trainModel(modelName: String, savePath: String)(train: (Array[Pcp], Array[Int]) => Classifier[Pcp]) =
    val (classes, samples) = DataSet.loadTrainData
    val model = train(samples, classes)

    logAccuracy(modelName, model)
    save(model, savePath)

  def logAccuracy(name: String, model: Classifier[Pcp]) =
    val accurcay = DataSet.loadTestData.map(tuple => tuple._1 -> accuracy(model, tuple._2))

    accurcay.foreach(tuple => logger.info(s"$name accuracy for ${tuple._1} test data is: ${tuple._2}"))
    logger.info(s"$name average accuracy for the whole test data is: ${accurcay.map(_._2).sum / accurcay.size}")

  def accuracy(classifier: Classifier[Pcp], testData: (Array[Int], Array[Pcp])) =
    Accuracy.of(testData._1, classifier.predict(testData._2))
