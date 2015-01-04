package com.proinnovate.activityscheduler

/**
 * This represents the state of an overall plan at any point in the planning process.
 *
 * @param unusedActivityPlaces all the activity places that have not been filled yet.
 * @param individualPlans a set of all the plans for all the individuals being scheduled across activities.
 */
case class OverallPlan(unusedActivityPlaces: Map[ActivityPlace,Int], individualPlans: Set[IndividualPlan]) {

  override def toString = s"OverallPlan(unusedPlaces=${unusedActivityPlaces.values.sum},$individualPlans)"

  lazy val fit: (Double,Double,Double,Double) = {
    val (minOut: Double, maxOut: Double, sumOut: Double) =
      individualPlans.foldLeft((Double.MaxValue, Double.MinValue, 0.0)) {
      case ((minIn: Double, maxIn: Double, sumIn: Double), plan) =>
        val minOut = math.min(minIn, plan.fit)
        val maxOut = math.max(maxIn, plan.fit)
        val sumOut = sumIn + plan.fit
        (minOut, maxOut, sumOut)
    }
    // Use the score of the minimum score for any one individual as the measure of the overall plan score.
    // In other words, focus on making sure that no one person misses out.
    val average = sumOut / individualPlans.size
    (minOut * 100 + average, minOut, average, maxOut)
  }

  lazy val notComplete = {
    // The overall plan is not complete if there exists an individual in the plan who has one or more unallocated
    // slots.
    individualPlans.exists(_.freeSlots.nonEmpty)
  }

  def withAllocation(activityPlace: ActivityPlace, individual: Individual): OverallPlan = {
    assert(unusedActivityPlaces.contains(activityPlace), "Can only allocate unused activity places!")
    val individualPlanOpt = individualPlans.find(_.individual == individual)
    assert(individualPlanOpt.isDefined, "The individual must exist!")
    val individualPlan = individualPlanOpt.get
    assert(individualPlan.freeSlots.contains(activityPlace.slot), "The individual plan must have a free slot for the activity!")
    val newUnusedActivityPlaces = unusedActivityPlacesAfterRemovingOne(activityPlace)
    val newIndividualPlans = individualPlansAfterAllocatingOne(individual, activityPlace)
    new OverallPlan(newUnusedActivityPlaces, newIndividualPlans)
  }

  def withRandomAllocatedActivity() = {
//    val activityPlace = randomUnallocatedActivityPlace
    val (individual,slot) = randomUnallocatedIndividualSlot
    val freeActivities = unusedActivityPlacesForSlot(slot).toSeq
    assert(freeActivities.size > 0, "There must be some free activity places in order to allocate them!")
    val selection = (freeActivities.size * math.random).toInt
    val activityPlace = freeActivities(selection)
    this.withAllocation(activityPlace, individual)
  }

  // PRIVATE

  private def unusedActivityPlacesForSlot(slot: Slot): Set[ActivityPlace] = {
    unusedActivityPlaces.keySet.filter(_.slot == slot)
  }

  private def unusedActivityPlacesAfterRemovingOne(activityPlace: ActivityPlace): Map[ActivityPlace,Int] = {
    val activityPlaceCountOpt = unusedActivityPlaces.get(activityPlace)
    assert(activityPlaceCountOpt.isDefined, "Cannot allocated a place that does not exists!")
    val activityPlaceCount = activityPlaceCountOpt.get
    if (activityPlaceCount > 1) {
      unusedActivityPlaces.map {
        case (`activityPlace`, count) => (activityPlace, count - 1)
        case x => x
      }
    } else {
      unusedActivityPlaces.collect {
        case (place, count) if place != activityPlace => (place,count)
      }
    }
  }

  private def individualPlansAfterAllocatingOne(individual: Individual, activityPlace: ActivityPlace): Set[IndividualPlan] = {
    val individualPlanOpt = individualPlans.find(_.individual == individual)
    assert(individualPlanOpt.isDefined, "There must be a plan for the individual to start with!")
    val individualPlan = individualPlanOpt.get
    val otherPlans = individualPlans - individualPlan
    val newIndividualPlan = individualPlan.withPlace(activityPlace)
    otherPlans + newIndividualPlan
  }

  private lazy val randomUnallocatedActivityPlace: ActivityPlace = {
    val keys = unusedActivityPlaces.keySet.toSeq
    val selection = (keys.size * math.random).toInt
    keys(selection)
  }

  private lazy val unallocatedIndividuals: Set[Individual] = {
    individualPlans.collect {
      case individualPlan if individualPlan.freeSlots.size > 0 => individualPlan.individual
    }
  }

  private lazy val randomUnallocatedIndividualSlot: (Individual,Slot) = {
    val individuals = unallocatedIndividuals.toSeq
    val iSelection = (individuals.size * math.random).toInt
    val individual = individuals(iSelection)
    val iPlan = individualPlans.find(_.individual == individual).get
    val freeSlots = iPlan.freeSlots.toSeq
    val sSelection = (freeSlots.size * math.random).toInt
    val slot = freeSlots(sSelection)
    (individual,slot)
  }

}