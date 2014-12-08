package com.proinnovate.activityscheduler

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class IndividualPlan(individual: Individual, activitySet: Set[ActivitySlot]) {

  /**
   * This value is an approximation of the suitability of the set of activities to the individual.
   * The more activities allocated with high ratings, the better the score.
   */
  lazy val fit: Double = {
    val ratings = for (activity <- activitySet.toSeq) yield individual.activityRatings(activity.name)
    10 * ratings.sum
  }

}
