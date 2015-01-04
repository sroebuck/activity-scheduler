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
//    val plan = bestOfSelection(randomPlan, 1000)
    val plan = randomBestOf(10,100)
    // Generate overall plan
    assert(plan.unusedActivityPlaces.size > 0)
    assert(plan.individualPlans.size > 0)
    // Test the fit
    logger.info(s"plan.fit = ${plan.fit}")
    assert(plan.fit._1 > 7)
    logger.info(s"plan = $plan")
  }

  def randomPlan() = {
    var plan = basePlan
    while(plan.notComplete) {
      plan = plan.withRandomAllocatedActivity()
    }
    plan
  }

  def randomBestOf(tryingNo: Int, keepingNo: Int): OverallPlan = {
    var plans = Seq.fill(keepingNo)(basePlan)
    while(plans(0).notComplete) {
      plans = bestNextRandomIteration(plans, tryingNo, keepingNo)
    }
    plans.sortBy(- _.fit._1).take(1).head
  }

  def bestNextRandomIteration(plans: Seq[OverallPlan], tryingNo: Int, keepingNo: Int): Seq[OverallPlan] = {
    val nextPlans = for {
      plan <- plans
      nextSteps <- 1 to tryingNo
    } yield plan.withRandomAllocatedActivity()
    val results = nextPlans.sortBy(- _.fit._1).take(keepingNo)
    // logger.debug(s"Iteration fit: ${results.head.fit}")
    results
  }

  def bestOfSelection(f: () => OverallPlan, sampleSize: Int): OverallPlan = {
    (0 to sampleSize).foldLeft(basePlan) {
      (best: OverallPlan, sample: Int) =>
        val plan = f()
        if (plan.fit._1 > best.fit._1) plan else best
    }
  }

}
