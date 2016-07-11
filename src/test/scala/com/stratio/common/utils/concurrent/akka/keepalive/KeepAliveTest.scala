/*
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.common.utils.concurrent.akka.keepalive

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import com.stratio.common.utils.concurrent.akka.keepalive.KeepAliveMaster.{DoCheck, HeartbeatLost}
import com.stratio.common.utils.concurrent.akka.keepalive.LiveMan.HeartBeat
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.duration._


@RunWith(classOf[JUnitRunner])
class KeepAliveTest extends TestKit(ActorSystem("KeepAliveSpec"))
  with FlatSpecLike with Matchers {

  class MonitoredActor(override val keepAliveId: Int, override val master: ActorRef) extends LiveMan[Int] {
    override val period: FiniteDuration = 100 milliseconds

    override def receive: Receive = PartialFunction.empty
  }


  "A LiveMan Actor" should "periodically send HearBeat message providing its id" in {

    val kaId = 1

    val liveMan: ActorRef = system.actorOf(Props(new MonitoredActor(kaId, testActor)))
    expectMsg(HeartBeat(kaId))

    system.stop(liveMan)
  }



  "A Master Actor" should "detect when a LiveManActor stops beating" in {

    val master: ActorRef = system.actorOf(KeepAliveMaster.props[Int](testActor))

    val liveMen: Seq[(Int, ActorRef)] = (1 to 5) map { idx =>
      master ! DoCheck(idx, 200 milliseconds)
      idx -> system.actorOf(Props(new MonitoredActor(idx, master)))
    }

    // All live actors are letting the master know that they're alive
    expectNoMsg(500 milliseconds)

    // Lets stop the first one
    system.stop(liveMen.head._2)

    // And wait for the right detection of its loss
    expectMsg(500 milliseconds, HeartbeatLost(liveMen.head._1))

    // Since the master is set to stop monitoring down actors and the rest of `liveMen` are working, no more loss
    // notifications should be expected.
    expectNoMsg(1 second)

    // Until another monitored actor is down
    val (lastId, lastActor) = liveMen.last

    system.stop(lastActor)

    expectMsg(500 milliseconds, HeartbeatLost(lastId))
    
    liveMen foreach {
      case (_, monitoredActor) => system.stop(monitoredActor)
    }

    system.stop(master)

  }


}
