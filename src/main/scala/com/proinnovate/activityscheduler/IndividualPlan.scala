package com.proinnovate.activityscheduler

import com.typesafe.scalalogging.LazyLogging

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class IndividualPlan(individual: Individual, activityPlaces: Set[ActivityPlace], overallSlots: Set[Slot]) extends LazyLogging {

  require( activityPlaces.map(_.slot).size == activityPlaces.size, "There must only be one allocation to any given slot")
  require( {
    val oneOnlyGroups = activityPlaces.toSeq.flatMap(_.activity.oneOnlyGroupOpt)
    oneOnlyGroups.toSet.size == oneOnlyGroups.size
  } , "Only one member of any oneOnlyGroup may exist in an individual plan")

  /**
   * This value is an approximation of the suitability of the set of activities to the individual.
   * The more activities allocated with high ratings, the better the score.
   */
  lazy val fit: Double = {
    val ratings = for (activityPlace <- activityPlaces.toSeq) yield individual.activityRatings(activityPlace.activity.name)
    val result = 10 * ratings.sum
//    logger.debug(s"Individual plan fit = $result")
    result
  }

  lazy val freeSlots: Set[Slot] = overallSlots -- activityPlaces.map(_.slot)

  lazy val activities: Set[Activity] = activityPlaces.map(_.activity)

  def withPlace(activityPlace: ActivityPlace) = copy(activityPlaces = activityPlaces + activityPlace)

  def hasFreeSlot(slot: Slot) = freeSlots.contains(slot)

}