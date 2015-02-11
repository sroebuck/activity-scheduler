package com.proinnovate.activityscheduler

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class Activity(name: String, min: Int = 2, max: Int = 6, oneOnlyGroupOpt: Option[String] = None) {
  require(Activity.universalNameSet.contains(name), s"Unknown activity name: $name")

  override def toString = name
}

object Activity {

  val universalNameSet = Set("Archery", "Trail Biking", "Ropes Course", "High Ropes", "Adventure Golf", "Baking",
    "Crafts", "Grylls Skylls", "Video workshop", "Mental Mayhem", "Indoor Games", "Games Hall", "Football",
    "Adventure Playground", "Another")

}
