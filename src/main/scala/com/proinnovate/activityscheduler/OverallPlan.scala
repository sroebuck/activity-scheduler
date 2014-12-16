package com.proinnovate.activityscheduler

/**
 * Created by sroebuck on 08/12/14.
 */
case class OverallPlan(activitySlots: Set[Activity], individualPlans: Set[IndividualPlan]) {

  lazy val fit: Double = {
    val (minOut: Double, maxOut: Double, sumOut: Double) =
      individualPlans.foldLeft((Double.MaxValue, Double.MinValue, 0.0)) {
      case ((minIn: Double, maxIn: Double, sumIn: Double), plan) =>
        val minOut = math.min(minIn, plan.fit)
        val maxOut = math.max(maxIn, plan.fit)
        val sumOut = sumIn + plan.fit
        (minOut, maxOut, sumOut)
    }
    // Use the score of the minimum score for any one individual as the measure of the overall plan score.
    // In other words, focus on making sure that no one person misses out.
    minOut
  }

}