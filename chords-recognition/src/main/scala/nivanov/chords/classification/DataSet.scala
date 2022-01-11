package nivanov.chords.classification

import nivanov.chords.audio.AudioUtil
import nivanov.chords.common.Config
import org.slf4j.LoggerFactory
import smile.data.DataFrame

import java.nio.file.{FileSystems, Files, Path}
import scala.collection.parallel.CollectionConverters.*
import scala.jdk.StreamConverters.*

object DataSet:
  val logger = LoggerFactory.getLogger(getClass)

  enum Instrument:
    case Accordion
    case Guitar
    case Piano
    case Violin
  end Instrument

  enum Chord:
    case A
    case Am
    case Bm
    case C
    case D
    case Dm
    case E
    case Em
    case F
    case G

    def mapToDataDirOf(dir: String) = dir + FileSystems.getDefault.getSeparator + toString.toLowerCase
  end Chord

  def loadTrainData = getProcessedData(Config.trainRawDataDir, Config.trainDataPath)
  def loadTestData = Instrument.values
    .map(instr => instr -> getProcessedData(Config.testRawDataDirFor(instr), Config.testDataPathFor(instr)))

  def getProcessedData(rawDir: String, processedPath: String) = readCsv(processedPath) match
    case Some(data) => data
    case None =>
      logger.info(s"Processing audio files from $rawDir ...")
      val start = System.currentTimeMillis()
      val data = processRawDataset(rawDir)
      val end = System.currentTimeMillis()
      logger.info(s"Processing took ${(end - start) / 1000f}s")

      writeCsv(processedPath, data)
      decompile(data)

  def readCsv(path: String) =
    if !Files.exists(Path.of(path)) then None
    else Some(decompile(smile.read.csv(path)))

  def writeCsv(path: String, data: DataFrame) = smile.write.csv(data, path)

  def processRawDataset(datasetDir: String): DataFrame = Chord.values.par
      .map(processRawFiles(datasetDir,_))
      .reduce(_ union _)

  def processRawFiles(datasetDir: String, chord: Chord) =
    val features = Files.list(Path.of(chord mapToDataDirOf datasetDir)).toScala(Iterable)
      .flatMap(AudioUtil.computePcpFrames(_))
      .toArray

    DataFrame.of(features.map(_ => Array(chord.ordinal)), "class") merge DataFrame.of(features)

  def decompile(dataFrame: DataFrame) =
    dataFrame.column("class").toIntArray -> dataFrame.drop(0).toArray
