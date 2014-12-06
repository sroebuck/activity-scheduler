package com.proinnovate.activityscheduler

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class ActivitySlot(name: String, slot: Slot, min: Int = 2, max: Int = 6) {
  require(ActivitySlot.universalNameSet.contains(name))
}

object ActivitySlot {

  val universalNameSet = Set("Archery","Trail Biking","Ropes","Tree Climb","High Ropes","Adventure Golf","Pitch & Putt","Orienteering","Games Hall","Football","Adventure Playground")

}
