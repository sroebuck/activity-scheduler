package com.proinnovate.activityscheduler

import org.joda.time.DateTime

object ActivityPlaceTest {

  val activityPlaceSet: Set[ActivityPlace] = {
    val slot1 = Slot("11am-12noon", new DateTime(2015, 2, 14, 11, 0), new DateTime(2015, 2, 14, 12, 0))
    val slot2 = Slot("12noon-1pm", new DateTime(2015, 2, 14, 12, 0), new DateTime(2015, 2, 14, 13, 0))
    val slot3 = Slot("2pm-3pm", new DateTime(2015, 2, 14, 14, 0), new DateTime(2015, 2, 14, 15, 0))
    val slot4 = Slot("3pm-4pm", new DateTime(2015, 2, 14, 15, 0), new DateTime(2015, 2, 14, 16, 0))

    val minMaxMappings = Map("Archery" -> (2, 6), "Trail Biking" -> (2,6), "Ropes Course" -> (2,7), "High Ropes" -> (2,6), 	"Adventure Golf" -> (2,6), "Baking" -> (2,12), "Crafts" -> (2,12), "Fire Starter" -> (2,12), "Video & Photography" -> (2,15),	"Mental Mayhem" -> (2,12), "Indoor Games" -> (5,15),	"Games Hall" -> (5,15), "Football" -> (4,14),	"Adventure Playground" -> (6,24), "Another" -> (2,8))
    val allActivityNames = minMaxMappings.map{ case (name, _) => name }.toSet
    val slot1ActivityNames = allActivityNames -- Set("Fire Starter", "Video & Photography", "Indoor Games", "Another")
    val slot1Places = slot1ActivityNames.map(name =>
      ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot1))
    val slot2ActivityNames = allActivityNames -- Set("Crafts", "Mental Mayhem", "Another")
    val slot2Places = slot2ActivityNames.map(name =>
      ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot2))
    val slot3ActivityNames = allActivityNames -- Set("Adventure Golf", "Mental Mayhem", "Another")
    val slot3Places = slot3ActivityNames.map(name =>
      ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot3))
    val slot4ActivityNames = allActivityNames -- Set("Baking", "Fire Starter", "Video & Photography", "Indoor Games", "Another")
    val slot4Places = slot4ActivityNames.map(name =>
      ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot4))
    (slot1Places ++ slot2Places ++ slot3Places ++ slot4Places).toSet
  }

}
