val scala2Version = "2.13.7"
val scala3Version = "3.1.0"

lazy val chordsRecognition = (project in file("chords-recognition"))
  .settings(
    scalaVersion := scala3Version,
    crossScalaVersions ++= Seq(scala2Version, scala3Version),

    libraryDependencies += ("com.github.haifengl" %% "smile-scala" % "2.6.0").cross(CrossVersion.for3Use2_13),
    libraryDependencies += "TarsosDSP" % "TarsosDSP" % "2.4" from "https://0110.be/releases/TarsosDSP/TarsosDSP-2.4/TarsosDSP-2.4.jar",

    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
    libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.32", "org.slf4j" % "slf4j-simple" % "1.7.32")
  ).disablePlugins(AssemblyPlugin)

lazy val game = (project in file("game"))
  .dependsOn(chordsRecognition)
  .settings(
    scalaVersion := scala3Version,
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies += "com.github.almasb" % "fxgl" % "11.17",

    assembly / assemblyJarName := "ChorDefense.jar",
    assembly / mainClass := Some("nivanov.chords.ChorDefense")
  )

ThisBuild / assemblyMergeStrategy  := {
    case PathList("META-INF", "MANIFEST.MF", xs @ _*) => MergeStrategy.discard
    case PathList("module-info.class") => MergeStrategy.discard
    case x if x.endsWith("/module-info.class") => MergeStrategy.discard
    case PathList("org", "xmlpull", "v1", xs @ _*) => MergeStrategy.first
    case x => MergeStrategy.defaultMergeStrategy(x)
}
