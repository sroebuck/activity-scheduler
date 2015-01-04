package com.proinnovate.activityscheduler

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class Activity(name: String, min: Int = 2, max: Int = 6) {
  require(Activity.universalNameSet.contains(name))

  override def toString = name
}

object Activity {

  val universalNameSet = Set("Archery","Trail Biking","Ropes","Tree Climb","High Ropes","Adventure Golf","Pitch & Putt","Orienteering","Games Hall","Football","Adventure Playground")

}
