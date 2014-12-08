package com.proinnovate.activityscheduler

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class ActivityRatings(ratingsMap: Map[String,Int]) {

  lazy val normalisedRatingsMap: Map[String,Double] = {
    val ratings = ratingsMap.values
    val min = ratings.min
    val max = ratings.max
    val normFunction = (rating: Int) => 1 - (rating - min) / (max - min).toDouble
    ratingsMap.map { case (s,i) => (s,normFunction(i)) }
  }

  /**
   * Return the normalised rating for any given activity in the map.  The normalised rating should be a
   * value from 0 to 1 where 1 is highest rating and 0 is lowest.
   * @param activity
   * @return
   */
  def apply(activity: String): Double = normalisedRatingsMap(activity)

}
