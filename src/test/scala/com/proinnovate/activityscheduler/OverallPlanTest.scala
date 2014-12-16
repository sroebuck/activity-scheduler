package com.proinnovate.activityscheduler

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{DiagrammedAssertions, FunSuite}

/**
 * Created by sroebuck on 10/12/14.
 */
class OverallPlanTest extends FunSuite with LazyLogging with DiagrammedAssertions {

  test("An overall plan can be constructed and results in a reasonable overall fit") {
    // Generate overall plan
    val plan = OverallPlan(Set(), Set())
    assert(plan.activitySlots.size > 0)
    assert(plan.individualPlans.size > 0)
    // Test the fit
    logger.info(s"plan.fit = ${plan.fit}")
    assert(plan.fit > 1000)
  }

}
