package com.proinnovate.activityscheduler

import org.joda.time.DateTime
import org.scalatest.FunSuite

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
class SlotTest extends FunSuite {

  test("Create some slots") {

    val slot1 = Slot("11am-12noon", new DateTime(2015,2,14,11,0),new DateTime(2015,2,14,12,0))
    val slot2 = Slot("12noon-1pm", new DateTime(2015,2,14,12,0),new DateTime(2015,2,14,13,0))
    val slot3 = Slot("2pm-3pm", new DateTime(2015,2,14,14,0),new DateTime(2015,2,14,15,0))
    val slot4 = Slot("3pm-4pm", new DateTime(2015,2,14,15,0),new DateTime(2015,2,14,16,0))

    val allSlots = Seq(slot1,slot2,slot3,slot4)

    println(allSlots)
  }

}
