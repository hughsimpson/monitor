package org.eigengo.monitor.agent.akka

import org.eigengo.monitor.{TestCounter, TestCounterInterface}
import akka.actor.Props

class JavaApiSpec  extends ActorCellMonitoringAspectSpec(Some("javaapi.conf")) {
  sequential
  import Aspects._


  "Counting the number of actors using the java api" should {

    // records the count of actors, grouped by simple class name
    "Record the actor count, and let us exclude an unnamed anonymous inner class actor" in {
      TestCounterInterface.clear()
      val akkaSystem = ExampleAkkaSystemWithJavaApiForTests.run(Array.empty)
      val parentPath = "parent.akka://helloakka/user"
      val actorPath = "path.akka://helloakka/user/$a"
      val propsClazz = "props.class org.eigengo.monitor.agent.akka.ExampleAkkaSystemWithJavaApiForTests$GreetPrinter"
      val className = "className.org.eigengo.monitor.agent.akka.ExampleAkkaSystemWithJavaApiForTests.GreetPrinter"

      val unnamedGreetPrinterTags = List(parentPath, actorPath, propsClazz, className)
      val namedGreetPrinterTags = List(parentPath, "path.akka://helloakka/user/greetPrinter", propsClazz, className)
      val greeterTags = List(parentPath, "path.akka://helloakka/user/greeter",
        "props.class org.eigengo.monitor.agent.akka.ExampleAkkaSystemWithJavaApiForTests$Greeter",
        "className.org.eigengo.monitor.agent.akka.ExampleAkkaSystemWithJavaApiForTests.Greeter")


      val greeter = akkaSystem.actorSelection("greeter")
      val namedGreetPrinter = akkaSystem.actorSelection("namedPrinter")
      val unnamedGreetPrinter = akkaSystem.actorOf(Props[ExampleAkkaSystemWithJavaApiForTests.GreetPrinter])


      Thread.sleep(100L)
      TestCounterInterface.foldlByAspect(actorCount)((a, _) => a) must containAllOf(Seq(
        TestCounter(actorCount, 2, unnamedGreetPrinterTags),
        TestCounter(actorCount, 1, namedGreetPrinterTags),
        TestCounter(actorCount, 1, greeterTags)))


      akkaSystem.shutdown()
      Thread.sleep(1000L)

      TestCounterInterface.foldlByAspect(actorCount)((a, _) => a) must containAnyOf(Seq(
        TestCounter(actorCount, 0, unnamedGreetPrinterTags),
        TestCounter(actorCount, 0, namedGreetPrinterTags),
        TestCounter(actorCount, 0, greeterTags)))

    }

  }

}
