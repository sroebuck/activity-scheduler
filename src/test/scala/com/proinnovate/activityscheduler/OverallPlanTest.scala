package com.proinnovate.activityscheduler

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{DiagrammedAssertions, FunSuite}

class OverallPlanTest extends FunSuite with LazyLogging with DiagrammedAssertions {

  val basePlan = {
    val activityPlaceSet = ActivityPlaceTest.activityPlaceSet
    val unusedActivityMap:Map[ActivityPlace,Int] = activityPlaceSet.map {
      activityPlace =>
        (activityPlace, activityPlace.activity.max)
    }.toMap
    val individualPlanSet = IndividualPlanTest.individualPlans
    OverallPlan(unusedActivityMap, individualPlanSet)
  }

  test("An overall plan can be constructed and results in a reasonable overall fit") {
    var plan = basePlan
    while(plan.notComplete) {
      plan = plan.withRandomAllocatedActivity()
    }
    // Generate overall plan
    assert(plan.unusedActivityPlaces.size > 0)
    assert(plan.individualPlans.size > 0)
    // Test the fit
    logger.info(s"plan.fit = ${plan.fit}")
    assert(plan.fit > 1000)
  }

}
