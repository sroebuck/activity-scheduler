package com.proinnovate.activityscheduler

import org.joda.time.DateTime

object ActivityPlaceTest {

  val activityPlaceSet: Set[ActivityPlace] = {
    val slot1 = Slot("11am-12noon", new DateTime(2015, 2, 14, 11, 0), new DateTime(2015, 2, 14, 12, 0))
    val slot2 = Slot("12noon-1pm", new DateTime(2015, 2, 14, 12, 0), new DateTime(2015, 2, 14, 13, 0))
    val slot3 = Slot("2pm-3pm", new DateTime(2015, 2, 14, 14, 0), new DateTime(2015, 2, 14, 15, 0))
    val slot4 = Slot("3pm-4pm", new DateTime(2015, 2, 14, 15, 0), new DateTime(2015, 2, 14, 16, 0))

    val minMaxMappings = Map("Archery" ->(2, 6), "Trail Biking" ->(2, 6), "Ropes Course" ->(2, 6), "Tree Climb" ->(1, 3), "High Ropes" ->(2, 6), "Adventure Golf" ->(2, 6), "Pitch & Putt" ->(2, 6), "Orienteering" ->(6, 20), "Games Hall" ->(5, 20), "Football" ->(4, 20), "Adventure Playground" ->(6, 20))

    val slot1ActivityNames = "Archery,Trail Biking,Ropes Course,Tree Climb,High Ropes,Adventure Golf,Orienteering,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot1Places = slot1ActivityNames.map(name => ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot1))

    val slot2ActivityNames = "Archery,Trail Biking,Ropes Course,Tree Climb,High Ropes,Adventure Golf,Pitch & Putt,Orienteering,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot2Places = slot2ActivityNames.map(name => ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot2))

    val slot3ActivityNames = "Archery,Trail Biking,Ropes Course,Tree Climb,High Ropes,Adventure Golf,Pitch & Putt,Orienteering,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot3Places = slot3ActivityNames.map(name => ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot3))

    val slot4ActivityNames = "Archery,Trail Biking,Ropes Course,Tree Climb,Adventure Golf,Orienteering,Games Hall,Football,Adventure Playground".split(',').toSeq
    val slot4Places = slot4ActivityNames.map(name => ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot4))

    (slot1Places ++ slot2Places ++ slot3Places ++ slot4Places).toSet
  }

}
