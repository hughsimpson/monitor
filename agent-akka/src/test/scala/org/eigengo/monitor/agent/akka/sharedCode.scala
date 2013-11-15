package org.eigengo.monitor.agent.akka

import org.eigengo.monitor.TestCounter

object sharedCode {
  val takeLHS : (TestCounter, TestCounter) => TestCounter = (a, _) => a

}
