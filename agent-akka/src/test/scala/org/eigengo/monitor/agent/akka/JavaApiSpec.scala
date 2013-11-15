package org.eigengo.monitor.agent.akka

import org.eigengo.monitor.{TestCounter, TestCounterInterface}
import akka.actor.Props

class JavaApiSpec  extends ActorCellMonitoringAspectSpec(Some("javaapi.conf")) {
  sequential
  import Aspects._


  "Counting the number of actors using the java api" should {

    // records the count of actors, grouped by simple class name
    "Record the actor count" in {
      val akkaSystem = HelloAkkaJava.run(Array.empty)
      val parentPath = "parent.akka://helloakka/user"
      val actorPath = "path.akka://helloakka/user/counter"
      val propsClazz = "props.class org.eigengo.monitor.agent.akka.HelloAkkaJava$GreetPrinter"
      val className = "className.org.eigengo.monitor.agent.akka.HelloAkkaJava.GreetPrinter"
      val simpleActorTags = List(parentPath, actorPath, propsClazz, className)
      TestCounterInterface.clear()
      val actorName = "counter"
      val greeter = akkaSystem.actorSelection("greeter")
      val greetPrinter = akkaSystem.actorOf(Props[HelloAkkaJava.GreetPrinter], actorName)


      Thread.sleep(100L)
      TestCounterInterface.foldlByAspect(actorCount)((a, _) => a) must contain(TestCounter(actorCount, 2, simpleActorTags))
      akkaSystem.shutdown()
      // we're sending gauge values here. We want the latest (hence our fold takes the 'head')
      TestCounterInterface.foldlByAspect(actorCount)((a, _) => a) must contain(TestCounter(actorCount, 0, simpleActorTags))
    }

  }

}
