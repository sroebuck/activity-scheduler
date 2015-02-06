package com.proinnovate.activityscheduler

import java.io.InputStream
import org.joda.time.DateTime

import scala.collection.immutable.Iterable

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
    val freeActivitiesPreFiltered = unusedActivityPlacesForSlot(slot).toSeq
    // Filter out activities that are already allocated for the individual
    val individualPlanOpt = individualPlans.find(_.individual == individual)
    assert(individualPlanOpt.isDefined, "There must be a plan for the individual to start with!")
    val freeActivities = freeActivitiesPreFiltered.filter(x => !individualPlanOpt.get.activities.contains(x.activity))
    assert(freeActivities.size > 0, "There must be some free activity places in order to allocate them!")
    val selection = (freeActivities.size * math.random).toInt
    val activityPlace = freeActivities(selection)
    this.withAllocation(activityPlace, individual)
  }

  lazy val individualPlansReport = {
    val nameMap = individualPlans.map( p => p.individual.uniqueName -> p).toMap
    val nameMapNamesInOrder = nameMap.keys.toSeq.sorted
    val individuals = for {
      name <- nameMapNamesInOrder
      plan <- nameMap.get(name)
    } yield {
      val places = plan.activityPlaces.toSeq.sortBy(_.slot.startDateTime.getMillis)
      (name + ":").padTo(25, ' ') + places.map(p => (p.activity.name + f" [${plan.individual.activityRatings(p.activity.name)}%.1f]").padTo(28, ' ')).mkString(" ")
    }
    individuals.mkString("\n")
  }

  lazy val activitySlotsReport = {
    val placeActivityTuple = for {
      individualPlan <- individualPlans
      individual = individualPlan.individual
      place <- individualPlan.activityPlaces
    } yield place -> individual
    val placeMap: Map[ActivityPlace, Individual] = placeActivityTuple.toMap
    val slots: Seq[Slot] = placeMap.toMap.keys.map(_.slot).toSet.toSeq.sortBy{x:Slot => x.startDateTime.getMillis}
    val activities = for {
      slot <- slots
      slotActivities = placeMap.keys.filter(_.slot == slot).toSeq.sortBy(_.activity.name)
      activity <- slotActivities
      individuals = placeActivityTuple.filter(_._1 == activity).map(_._2)
    } yield {
      slot.name + ": " + activity.activity.name + " - " + individuals.map(_.uniqueName).mkString(",")
    }
    activities.mkString("\n")
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

object OverallPlan {

  def randomPlan(start: OverallPlan) = {
    var plan = start
    while(plan.notComplete) {
      plan = plan.withRandomAllocatedActivity()
    }
    plan
  }

  def randomBestOf(start: OverallPlan, tryingNo: Int, keepingNo: Int): OverallPlan = {
    var plans = Seq.fill(keepingNo)(start)
    while(plans(0).notComplete) {
      plans = bestNextRandomIteration(plans, tryingNo, keepingNo)
    }
    plans.sortBy(- _.fit._1).take(1).head
  }

  def bestNextRandomIteration(plans: Seq[OverallPlan], tryingNo: Int, keepingNo: Int): Seq[OverallPlan] = {
    val nextPlans = for {
      plan <- plans
      nextSteps <- 1 to tryingNo
    } yield plan.withRandomAllocatedActivity()
    val results = nextPlans.sortBy(- _.fit._1).take(keepingNo)
    // logger.debug(s"Iteration fit: ${results.head.fit}")
    results
  }

  def bestOfSelection(start: OverallPlan, f: () => OverallPlan, sampleSize: Int): OverallPlan = {
    (0 to sampleSize).foldLeft(start) {
      (best: OverallPlan, sample: Int) =>
        val plan = f()
        if (plan.fit._1 > best.fit._1) plan else best
    }
  }

  def realPlanFromActivitiesAndIndividualStream(inputStream: InputStream): OverallPlan = {
    val overallSlots = {
      val slot1 = Slot("11am-12noon", new DateTime(2015,2,14,11,0),new DateTime(2015,2,14,12,0))
      val slot2 = Slot("12noon-1pm", new DateTime(2015,2,14,12,0),new DateTime(2015,2,14,13,0))
      val slot3 = Slot("2pm-3pm", new DateTime(2015,2,14,14,0),new DateTime(2015,2,14,15,0))
      val slot4 = Slot("3pm-4pm", new DateTime(2015,2,14,15,0),new DateTime(2015,2,14,16,0))
      Set(slot1,slot2,slot3,slot4)
    }

    val activityPlaceSet: Set[ActivityPlace] = {
      val slot1 = Slot("11am-12noon", new DateTime(2015, 2, 14, 11, 0), new DateTime(2015, 2, 14, 12, 0))
      val slot2 = Slot("12noon-1pm", new DateTime(2015, 2, 14, 12, 0), new DateTime(2015, 2, 14, 13, 0))
      val slot3 = Slot("2pm-3pm", new DateTime(2015, 2, 14, 14, 0), new DateTime(2015, 2, 14, 15, 0))
      val slot4 = Slot("3pm-4pm", new DateTime(2015, 2, 14, 15, 0), new DateTime(2015, 2, 14, 16, 0))
      val minMaxMappings = Map("Archery" -> (2, 6), "Trail Biking" -> (2,6), "Ropes Course" -> (2,6), "High Ropes" -> (2,6), 	"Adventure Golf" -> (2,6), "Baking" -> (2,6), "Crafts" -> (2,6), "Fire Starter" -> (2,6), "Video & Photography" -> (2,6),	"Mental Mayhem" -> (2,6), "Indoor Games" -> (5,20),	"Games Hall" -> (5,20), "Football" -> (4,20),	"Adventure Playground" -> (6,20), "Another" -> (2,8))
      val slot1ActivityNames: Seq[String] = minMaxMappings.map{ case (name, _) => name }.toSeq
      val slot1Places = slot1ActivityNames.map(name =>
        ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot1))
      val slot2ActivityNames: Seq[String] = minMaxMappings.map{ case (name, _) => name }.toSeq
      val slot2Places = slot2ActivityNames.map(name =>
        ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot2))
      val slot3ActivityNames: Seq[String] = minMaxMappings.map{ case (name, _) => name }.toSeq
      val slot3Places = slot3ActivityNames.map(name =>
        ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot3))
      val slot4ActivityNames: Seq[String] = minMaxMappings.map{ case (name, _) => name }.toSeq
      val slot4Places = slot4ActivityNames.map(name =>
        ActivityPlace(Activity(name, minMaxMappings(name)._1, minMaxMappings(name)._2), slot4))
      (slot1Places ++ slot2Places ++ slot3Places ++ slot4Places).toSet
    }

    val realIndividualPlans: Set[IndividualPlan] = {
      val (_, individuals) = ChoiceReader.readActivitiesAndIndividuals(inputStream)
      individuals.map {
        individual =>
          val activityPlaces = Set[ActivityPlace]()
          IndividualPlan(individual, activityPlaces, overallSlots)
      }
    }

    val realDataStartPlan = {
      val unusedActivityMap:Map[ActivityPlace,Int] = activityPlaceSet.map {
        activityPlace =>
          (activityPlace, activityPlace.activity.max)
      }.toMap
      OverallPlan(unusedActivityMap, realIndividualPlans)
    }

    OverallPlan.randomBestOf(realDataStartPlan, 10,100)
  }

}