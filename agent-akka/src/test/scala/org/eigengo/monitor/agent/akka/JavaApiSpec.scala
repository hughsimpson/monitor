package org.eigengo.monitor.agent.akka

import org.eigengo.monitor.{TestCounter, TestCounterInterface}
import akka.actor.Props
import org.eigengo.monitor.agent.akka.HelloAkkaJava.Die

class JavaApiSpec  extends ActorCellMonitoringAspectSpec(Some("javaapi.conf")) {
  sequential
  import Aspects._


  "Counting the number of actors using the java api" should {

    // records the count of actors, grouped by simple class name
    "Record the actor count" in {
      TestCounterInterface.clear()
      val akkaSystem = HelloAkkaJava.run(Array.empty)
      val parentPath = "parent.akka://helloakka/user"
      val actorPath = "path.akka://helloakka/user/$a"
      val propsClazz = "props.class org.eigengo.monitor.agent.akka.HelloAkkaJava$GreetPrinter"
      val className = "className.org.eigengo.monitor.agent.akka.HelloAkkaJava.GreetPrinter"

      val unnamedGreetPrinterTags = List(parentPath, actorPath, propsClazz, className)
      val namedGreetPrinterTags = List(parentPath, "path.akka://helloakka/user/greetPrinter", propsClazz, className)
      val greeterTags = List(parentPath, "path.akka://helloakka/user/greeter",
        "props.class org.eigengo.monitor.agent.akka.HelloAkkaJava$Greeter",
        "className.org.eigengo.monitor.agent.akka.HelloAkkaJava.Greeter")


      val greeter = akkaSystem.actorSelection("greeter")
      val namedGreetPrinter = akkaSystem.actorSelection("namedPrinter")
      val unnamedGreetPrinter = akkaSystem.actorOf(Props[HelloAkkaJava.GreetPrinter])


      Thread.sleep(100L)
      TestCounterInterface.foldlByAspect(actorCount)((a, _) => a) must containAllOf(Seq(
        TestCounter(actorCount, 2, unnamedGreetPrinterTags),
        TestCounter(actorCount, 1, namedGreetPrinterTags),
        TestCounter(actorCount, 1, greeterTags)))

      greeter ! "die"
      namedGreetPrinter ! "die"
      unnamedGreetPrinter ! "die"
      Thread.sleep(700L)
      // we're sending gauge values here. We want the latest (hence our fold takes the 'head')
      TestCounterInterface.foldlByAspect(actorCount)((a, _) => a) must containAllOf(Seq(
        TestCounter(actorCount, 0, unnamedGreetPrinterTags),
        TestCounter(actorCount, 0, namedGreetPrinterTags),
        TestCounter(actorCount, 0, greeterTags)))
      akkaSystem.shutdown()
      success
    }

  }

}
