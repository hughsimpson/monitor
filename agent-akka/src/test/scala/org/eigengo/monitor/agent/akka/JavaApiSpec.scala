package org.eigengo.monitor.agent.akka

class JavaApiSpec  extends ActorCellMonitoringAspectSpec(Some("unfiltered.conf")) {
  sequential
  import Aspects._

  val akkaClass = new HelloAkkaJava()

  "Counting the number of actors using the java api" should {
    "fail" in {
      failure
    }
  }
}
