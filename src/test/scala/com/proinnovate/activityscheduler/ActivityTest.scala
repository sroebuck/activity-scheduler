package com.proinnovate.activityscheduler

import org.joda.time.DateTime
import org.scalatest.FunSuite

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
class ActivityTest extends FunSuite {

  val slot1 = Slot("11am-12noon", new DateTime(2015,2,14,11,0),new DateTime(2015,2,14,12,0))
  val slot2 = Slot("12noon-1pm", new DateTime(2015,2,14,12,0),new DateTime(2015,2,14,13,0))
  val slot3 = Slot("2pm-3pm", new DateTime(2015,2,14,14,0),new DateTime(2015,2,14,15,0))
  val slot4 = Slot("3pm-4pm", new DateTime(2015,2,14,15,0),new DateTime(2015,2,14,16,0))

  test("Create some activities") {
    val minMaxMappings= Map("Archery"->(2,6),"Trail Biking"->(2,6),"Ropes Course"->(2,6),"Grylls Skylls"->(2,6),"High Ropes"->(2,6),"Adventure Golf"->(2,6),"Video workshop"->(2,6),"Baking"->(6,20),"Games Hall"->(5,20),"Football"->(4,20),"Adventure Playground"->(6,20))

    val slot1ActivityNames = "Archery,Trail Biking,Ropes Course,Grylls Skylls,High Ropes,Adventure Golf,Baking,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot1Activities = slot1ActivityNames.map(name => Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2))

    val slot2ActivityNames = "Archery,Trail Biking,Ropes Course,Grylls Skylls,High Ropes,Adventure Golf,Video workshop,Baking,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot2Activities = slot2ActivityNames.map(name => Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2))

    val slot3ActivityNames = "Archery,Trail Biking,Ropes Course,Grylls Skylls,High Ropes,Adventure Golf,Video workshop,Baking,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot3Activities = slot3ActivityNames.map(name => Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2))

    val slot4ActivityNames = "Archery,Trail Biking,Ropes Course,Grylls Skylls,Adventure Golf,Baking,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot4Activities = slot4ActivityNames.map(name => Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2))

  }

}
