package com.proinnovate.activityscheduler

import com.typesafe.scalalogging.LazyLogging

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class IndividualPlan(individual: Individual, activityPlaces: Set[ActivityPlace], overallSlots: Set[Slot]) extends LazyLogging {

  assert( activityPlaces.map(_.slot).size == activityPlaces.size, "There must only be one allocation to any given slot")

  /**
   * This value is an approximation of the suitability of the set of activities to the individual.
   * The more activities allocated with high ratings, the better the score.
   */
  lazy val fit: Double = {
    val ratings = for (activityPlace <- activityPlaces.toSeq) yield individual.activityRatings(activityPlace.activity.name)
    val result = 10 * ratings.sum
    logger.info(s"Individual plan fit = $result")
    result
  }

  lazy val freeSlots: Set[Slot] = overallSlots -- activityPlaces.map(_.slot)

  def withPlace(activityPlace: ActivityPlace) = copy(activityPlaces = activityPlaces + activityPlace)

  def hasFreeSlot(slot: Slot) = freeSlots.contains(slot)

}