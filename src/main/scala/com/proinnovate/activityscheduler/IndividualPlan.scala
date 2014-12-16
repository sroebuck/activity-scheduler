package com.proinnovate.activityscheduler

import com.typesafe.scalalogging.LazyLogging

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class IndividualPlan(individual: Individual, activitySet: Set[Activity]) extends LazyLogging {

  /**
   * This value is an approximation of the suitability of the set of activities to the individual.
   * The more activities allocated with high ratings, the better the score.
   */
  lazy val fit: Double = {
    val ratings = for (activity <- activitySet.toSeq) yield individual.activityRatings(activity.name)
    val result = 10 * ratings.sum
    logger.info(s"Individual plan fit = $result")
    result
  }

}
