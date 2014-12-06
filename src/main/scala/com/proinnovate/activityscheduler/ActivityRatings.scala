package com.proinnovate.activityscheduler

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class ActivityRatings(ratingsMap: Map[String,Int]) {

  lazy val normalizedRatingsMap: Map[String,Double] = {
    val ratings = ratingsMap.values
    val min = ratings.min
    val max = ratings.max
    val normFunction = (rating: Int) => 1 - (rating - min) / (max - min).toDouble
    ratingsMap.map { case (s,i) => (s,normFunction(i)) }
  }

}
