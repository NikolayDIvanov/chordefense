package nivanov.chords.common

import nivanov.chords.classification.DataSet.Instrument
import java.util.Properties

object Config:
  lazy val props = new Properties { load(getClass.getClassLoader.getResourceAsStream("config.properties")) }
  val resourcesDir = "chords-recognition/src/main/resources"

  def trainRawDataDir = props.getProperty("data.train.raw.dir")
  def testRawDataDirFor(instr: Instrument) = props.getProperty(s"data.test.raw.${instr.toString.toLowerCase}.dir")

  def testDataPathFor(instr: Instrument) = s"$resourcesDir/data/test-${instr.toString.toLowerCase}.csv"
  def trainDataPath = s"$resourcesDir/data/train.csv"
  def svmModelPath = s"$resourcesDir/model/svm.xml"
  def knnModelPath = s"$resourcesDir/model/knn.xml"
