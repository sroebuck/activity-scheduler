package com.proinnovate.activityscheduler

import java.io.InputStream

import akka.actor.Actor
import spray.http.StatusCodes
import spray.routing.HttpService

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

  import play.api.libs.json._

  implicit val activityPlaceWrites = new Writes[ActivityPlace] {
    def writes(activityPlace: ActivityPlace) = Json.obj(
      "activity" -> activityPlace.activity.name,
      "slot" -> activityPlace.slot.name
    )
  }
  implicit val individualWrites = new Writes[Individual] {
    def writes(individual: Individual) = Json.obj(
      "name" -> individual.uniqueName,
      "group" -> individual.groupId,
      "ratings" -> individual.activityRatings.ratingsMap
    )
  }
  implicit val individualPlanWrites = new Writes[IndividualPlan] {
    def writes(individualPlan: IndividualPlan) = Json.obj(
      "individual" -> individualPlan.individual,
      "places" -> individualPlan.activityPlaces
    )
  }
  implicit val overallPlanWrites = new Writes[OverallPlan] {
    def writes(overallPlan: OverallPlan) = Json.obj(
      "plans" -> overallPlan.individualPlans
    )
  }

  lazy val plan = {
    val inputStream: InputStream = getClass.getResourceAsStream("activity-choices.csv")
    OverallPlan.realPlanFromActivitiesAndIndividualStream(inputStream)
  }

  val myRoute =
    path("") {
      redirect("/index.html", StatusCodes.PermanentRedirect)
    } ~
    path("test.json") {
      get {
        complete {
          Json.stringify(Json.toJson(plan))
        }
      }
    } ~
    pathPrefix("") {
      getFromDirectory("react/dist")
    }
}
