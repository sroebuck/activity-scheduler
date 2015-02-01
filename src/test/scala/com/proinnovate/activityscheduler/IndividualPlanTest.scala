package com.proinnovate.activityscheduler

object IndividualPlanTest {

  val individualPlans: Set[IndividualPlan] = {
    IndividualTest.individualSet.map {
      individual =>
        val activityPlaces = Set[ActivityPlace]()
        IndividualPlan(individual, activityPlaces, SlotTest.overallSlots)
    }
  }

}
