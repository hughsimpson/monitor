object BuildSettings {
  import sbt._
  import Keys._
  import com.typesafe.sbt.SbtAspectj.{ Aspectj, aspectjSettings }
  import com.typesafe.sbt.SbtAspectj.AspectjKeys.{ compileOnly, weaverOptions, aspectjDirectory }
  import org.scalastyle.sbt.ScalastylePlugin

  lazy val buildSettings = Defaults.defaultSettings ++ Publish.settings ++ ScalastylePlugin.Settings ++ Seq(
  	org.scalastyle.sbt.PluginKeys.config := file("project/scalastyle-config.xml"),
    scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.7", "-deprecation", "-unchecked", "-Ywarn-dead-code"),
    scalacOptions in (Compile, doc) <++= (name in (Compile, doc), version in (Compile, doc)) map DefaultOptions.scaladoc,
    //javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7", "-Xlint:unchecked", "-Xlint:deprecation", "-Xlint:-options"),
    //scalacOptions in doc := Seq(),
    //javacOptions in doc := Seq("-source", "1.7"),
    javaOptions += "-Xmx2G",
    //scalaVersion := "2.11.0-M5",
    outputStrategy := Some(StdoutOutput),
    // fork := true,
    maxErrors := 1,
    // addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full),
    resolvers ++= Seq(
      Resolver.mavenLocal,
      Resolver.sonatypeRepo("releases"),
      Resolver.typesafeRepo("releases"),
      Resolver.typesafeRepo("snapshots"),
      Resolver.sonatypeRepo("snapshots")
    ),
    parallelExecution in Test := false
  )

  lazy val aspectjCompileSettings = aspectjSettings ++ Seq(
    // only compile the aspects (no weaving)
    compileOnly in Aspectj := true,
    aspectjDirectory in Aspectj <<= crossTarget,

    // add the compiled aspects as products
    products in Compile <++= products in Aspectj
  )
  val compile_clojure_task = TaskKey[Unit]("compile_clojure_task")
  lazy val compileClojure = compile_clojure_task := {
    import clojure.lang._
    import clojure.lang.{RT, Var, Compiler}
    import java.io.StringReader
    import scala.io.Source
    val notionalScriptFileName="org.eigengo.monitor.output.datadog.DataDogCounterInterface"
    val clojureScript = Source.fromInputStream(getClass.getResourceAsStream("../output-datadog/src/main/resources/DataDogCounterInterface.clj")).mkString
    val outputDirectory="../output-datadog/target/"

    val compilePath : Var = RT.`var`("clojure.core", "*compile-path*");
    val compileFiles : Var = RT.`var`("clojure.core", "*compile-files*");

    Var.pushThreadBindings(RT.map(compilePath, outputDirectory, compileFiles, java.lang.Boolean.TRUE))
    clojure.lang.Compiler.compile(new StringReader(clojureScript), notionalScriptFileName, notionalScriptFileName)
    Var.popThreadBindings()
  }

}
