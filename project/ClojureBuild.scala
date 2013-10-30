//object ClojureBuild {
//  import _root_.clojure.lang._
//  import _root_.clojure.lang.{RT, Var, Compiler}
//  import java.io.StringReader
//  import scala.io.Source
//  def compileClojure {
//    val notionalScriptFileName="DataDogCounterInterface.clj"
//    val clojureScript = Source.fromInputStream(getClass.getResourceAsStream(notionalScriptFileName)).mkString
//    val outputDirectory="src/../target/"
//
//    val compilePath : Var = RT.`var`("clojure.core", "*compile-path*");
//    val compileFiles : Var = RT.`var`("clojure.core", "*compile-files*");
//
//    Var.pushThreadBindings(RT.map(compilePath, outputDirectory, compileFiles, true))
//    Compiler.compile(new StringReader(clojureScript), notionalScriptFileName, notionalScriptFileName)
//    Var.popThreadBindings()
//  }
//}