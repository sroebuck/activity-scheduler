package com.proinnovate.activityscheduler

import org.joda.time.DateTime
import org.scalatest.FunSuite

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
class ActivitySlotTest extends FunSuite {

  val slot1 = Slot("11am-12noon", new DateTime(2015,2,14,11,0),new DateTime(2015,2,14,12,0))
  val slot2 = Slot("12noon-1pm", new DateTime(2015,2,14,12,0),new DateTime(2015,2,14,13,0))
  val slot3 = Slot("2pm-3pm", new DateTime(2015,2,14,14,0),new DateTime(2015,2,14,15,0))
  val slot4 = Slot("3pm-4pm", new DateTime(2015,2,14,15,0),new DateTime(2015,2,14,16,0))

  test("Create some activities") {
    val minMaxMappings= Map("Archery"->(2,6),"Trail Biking"->(2,6),"Ropes"->(2,6),"Tree Climb"->(2,6),"High Ropes"->(2,6),"Adventure Golf"->(2,6),"Pitch & Putt"->(2,6),"Orienteering"->(6,20),"Games Hall"->(5,20),"Football"->(4,20),"Adventure Playground"->(6,20))

    val slot1ActivityNames = "Archery,Trail Biking,Ropes,Tree Climb,High Ropes,Adventure Golf,Orienteering,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot1Activities = slot1ActivityNames.map(name => ActivitySlot(name, slot1, minMaxMappings(name)._1, minMaxMappings(name)._2))
    println(slot1Activities)

    val slot2ActivityNames = "Archery,Trail Biking,Ropes,Tree Climb,High Ropes,Adventure Golf,Pitch & Putt,Orienteering,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot2Activities = slot2ActivityNames.map(name => ActivitySlot(name, slot2, minMaxMappings(name)._1, minMaxMappings(name)._2))
    println(slot2Activities)

    val slot3ActivityNames = "Archery,Trail Biking,Ropes,Tree Climb,High Ropes,Adventure Golf,Pitch & Putt,Orienteering,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot3Activities = slot3ActivityNames.map(name => ActivitySlot(name, slot3, minMaxMappings(name)._1, minMaxMappings(name)._2))
    println(slot3Activities)

    val slot4ActivityNames = "Archery,Trail Biking,Ropes,Tree Climb,Adventure Golf,Orienteering,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot4Activities = slot4ActivityNames.map(name => ActivitySlot(name, slot4, minMaxMappings(name)._1, minMaxMappings(name)._2))
    println(slot4Activities)

  }

}