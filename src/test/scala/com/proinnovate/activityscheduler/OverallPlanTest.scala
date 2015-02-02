package com.proinnovate.activityscheduler

import java.io.InputStream

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{DiagrammedAssertions, FunSuite}

class OverallPlanTest extends FunSuite with LazyLogging with DiagrammedAssertions {

  val randomStartPlan = {
    val activityPlaceSet = ActivityPlaceTest.activityPlaceSet
    val unusedActivityMap:Map[ActivityPlace,Int] = activityPlaceSet.map {
      activityPlace =>
        (activityPlace, activityPlace.activity.max)
    }.toMap
    val individualPlanSet = IndividualPlanTest.individualPlans
    OverallPlan(unusedActivityMap, individualPlanSet)
  }

  test("An overall plan can be constructed and results in a reasonable overall fit") {
//    val plan = bestOfSelection(randomPlan, 1000)
    val plan = OverallPlan.randomBestOf(randomStartPlan, 10,100)
    // Generate overall plan
    assert(plan.unusedActivityPlaces.size > 0)
    assert(plan.individualPlans.size > 0)
    // Test the fit
    logger.info(s"plan.fit = ${plan.fit}")
    assert(plan.fit._1 > 7)
    logger.info(s"plan =\n${plan.individualPlansReport}")
    logger.info(s"activities =\n${plan.activitySlotsReport}")
  }
  
  test("Try another plan with some more realistic input data") {
    val inputStream: InputStream = getClass.getResourceAsStream("activity-choices.csv")
    val plan = OverallPlan.realPlanFromActivitiesAndIndividualStream(inputStream)
    assert(plan.unusedActivityPlaces.size > 0)
    assert(plan.individualPlans.size > 0)
    // Test the fit
    logger.info(s"plan.fit = ${plan.fit}")
    assert(plan.fit._1 > 7)
    logger.info(s"plan =\n${plan.individualPlansReport}")
    logger.info(s"activities =\n${plan.activitySlotsReport}")
  }

}
