package com.proinnovate.activityscheduler

/**
 * Created by sroebuck on 08/12/14.
 */
case class OverallPlan(individualPlans: Set[IndividualPlan]) {

  lazy val fit: Double = {
    0
  }

}