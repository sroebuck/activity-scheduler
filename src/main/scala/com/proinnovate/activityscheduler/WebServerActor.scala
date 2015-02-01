package com.proinnovate.activityscheduler

import akka.actor.Actor
import spray.http.StatusCodes
import spray.routing.HttpService
import spray.http.MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class WebServerActor extends Actor with WebService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait WebService extends HttpService {

  val myRoute =
    path("") {
      redirect("/index.html", StatusCodes.PermanentRedirect)
    } ~
    pathPrefix("") {
      getFromDirectory("react/dist")
    }
}
