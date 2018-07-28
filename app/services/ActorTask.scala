package services

import akka.actor.ActorSystem
import javax.inject.Inject

import scala.concurrent.ExecutionContext

class ActorTask @Inject() (actorSystem: ActorSystem )(implicit executionContext: ExecutionContext) {

  //設定啟動application後開始執行時間，每幾分鐘執行
//  actorSystem.scheduler.schedule(initialDelay = 1.seconds, interval = 10.seconds) {
    // the block of code that will be executed
//    println("Executing something...")
//    new dd().onStart()
//    new dd().onStop()
//  }
}

