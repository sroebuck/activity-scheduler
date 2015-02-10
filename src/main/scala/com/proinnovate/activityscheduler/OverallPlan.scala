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
case class OverallPlan(unusedActivityPlaces: Map[ActivityPlace,Int],
                       individualPlans: Set[IndividualPlan],
                       activityPlaceGroupMembers: Map[ActivityPlace, Seq[String]] = Map.empty) {

  override def toString = s"OverallPlan(unusedPlaces=${unusedActivityPlaces.values.sum},$individualPlans)"

  case class Fit(bestOverallFit: Double,
                 bestGrowingFit: Double,
                 min: Double,
                 average: Double,
                 max: Double)

  lazy val fit: Fit = {
    val (minOut: Double, maxOut: Double, sumOut: Double) =
      individualPlans.foldLeft((Double.MaxValue, Double.MinValue, 0.0)) {
      case ((minIn: Double, maxIn: Double, sumIn: Double), plan) =>
        val minOut = math.min(minIn, plan.fit)
        val maxOut = math.max(maxIn, plan.fit)
        val sumOut = sumIn + plan.fit
        (minOut, maxOut, sumOut)
    }
    val average = sumOut / individualPlans.size
    // Calculate the number of minimum allocation places that have not been filled yet, but don't count this at all
    // if no places have been taken yet. This should count against a good score. Note that this creates local maxima
    // and should not be used to grow towards the final goal.
    val totalUnderMinimum = unusedActivityPlaces.map {
      case (place, countRemaining) =>
        val max = place.activity.max
        val used = max - countRemaining
        val underMinimum = if (used == 0) 0 else place.activity.min - used
        underMinimum
    }.sum
    val groupMemberMatches = activityPlaceGroupMembers.map {
      case (place, groups) => groups.size - groups.toSet.size
    }.sum
    // Use the score of the minimum score for any one individual as the measure of the overall plan score.
    // In other words, focus on making sure that no one person misses out.
    val averageWeightedByIndMin = minOut * 100 + average
    // Account for the number of missing minimum places
    val weightedAverageAccountingForGroups = averageWeightedByIndMin + (groupMemberMatches / 10)
    val bestOverallFit = weightedAverageAccountingForGroups - (100 * totalUnderMinimum)
    Fit(
      bestGrowingFit = weightedAverageAccountingForGroups,
      bestOverallFit = bestOverallFit,
      min = minOut,
      average = average,
      max = maxOut
    )
  }

//  lazy val activityPlaceGroupMembers: Seq[(ActivityPlace, Seq[String])] = {
//    val placeGroupTuple: Set[(ActivityPlace, String)] = for {
//      individualPlan <- individualPlans
//      groupId = individualPlan.individual.groupId
//      place <- individualPlan.activityPlaces
//    } yield (place, groupId)
//    placeGroupTuple.groupBy(_._1).map( x => (x._1, x._2.map(_._2).toSeq)).toSeq
//  }

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
    val newGroupMembers = activityPlaceGroupMembers.updated(activityPlace,
      activityPlaceGroupMembers.getOrElse(activityPlace, Seq[String]()) :+ individual.groupId)
    new OverallPlan(newUnusedActivityPlaces, newIndividualPlans, newGroupMembers)
  }

  def withRandomAllocatedActivity() = {
//    val activityPlace = randomUnallocatedActivityPlace
    val (individual,slot) = randomUnallocatedIndividualSlot
    val freeActivitiesPreFiltered = unusedActivityPlacesForSlot(slot).toSeq
    // Filter out activities that are already allocated for the individual
    val individualPlanOpt = individualPlans.find(_.individual == individual)
    assert(individualPlanOpt.isDefined, "There must be a plan for the individual to start with!")
    val individualPlan = individualPlanOpt.get
    // Remove any activities that are already in the individual's plan...
    val freeActivities = freeActivitiesPreFiltered.filter(x => !individualPlan.activities.contains(x.activity))
    // Remove any activities that are duplicates in oneGroupOnly groups...
    val existingOneOnlyGroups: Set[String] = individualPlan.activities.flatMap(_.oneOnlyGroupOpt)
    val nonGroupDuplicateActivities = freeActivities.filter(x => !existingOneOnlyGroups.contains(x.activity.oneOnlyGroupOpt.getOrElse("")))
    assert(nonGroupDuplicateActivities.size > 0, "There must be some free activity places in order to allocate them!")
    val selection = (nonGroupDuplicateActivities.size * math.random).toInt
    val activityPlace = nonGroupDuplicateActivities(selection)
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
    plans.sortBy(- _.fit.bestGrowingFit).take(1).head
  }

  def bestNextRandomIteration(plans: Seq[OverallPlan], tryingNo: Int, keepingNo: Int): Seq[OverallPlan] = {
    val nextPlans = for {
      plan <- plans.par
      nextSteps <- 1 to tryingNo
    } yield plan.withRandomAllocatedActivity()
    val results = nextPlans.seq.sortBy(- _.fit.bestGrowingFit).take(keepingNo)
    // logger.debug(s"Iteration fit: ${results.head.fit}")
    results
  }

  def bestOfSelection(start: OverallPlan, f: () => OverallPlan, sampleSize: Int): OverallPlan = {
    (0 to sampleSize).foldLeft(start) {
      (best: OverallPlan, sample: Int) =>
        val plan = f()
        if (plan.fit.bestGrowingFit > best.fit.bestGrowingFit) plan else best
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
      val mappings = Map("Archery" -> (3, 6, Some("A")), "Trail Biking" -> (3,6, Some("A")),
          "Ropes Course" -> (4,7, Some("A")), "High Ropes" -> (4,6, Some("A")), 	"Adventure Golf" -> (3,6,None),
          "Baking" -> (8,12,None), "Crafts" -> (6,12,None), "Fire Starter" -> (6,12,None),
          "Video & Photography" -> (8,15,None),	"Mental Mayhem" -> (8,12,None), "Indoor Games" -> (8,15,None),
          "Games Hall" -> (10,15,None), "Football" -> (8,14,None),	"Adventure Playground" -> (8,24,None),
          "Another" -> (2,8,None))
      val allActivityNames = mappings.map{ case (name, _) => name }.toSet
      val slot1ActivityNames = allActivityNames -- Set("Fire Starter", "Video & Photography", "Indoor Games", "Another")
      val slot1Places = slot1ActivityNames.map(name =>
        ActivityPlace(Activity(name, mappings(name)._1, mappings(name)._2, mappings(name)._3), slot1))
      val slot2ActivityNames = allActivityNames -- Set("Crafts", "Mental Mayhem", "Another")
      val slot2Places = slot2ActivityNames.map(name =>
        ActivityPlace(Activity(name, mappings(name)._1, mappings(name)._2, mappings(name)._3), slot2))
      val slot3ActivityNames = allActivityNames -- Set("Adventure Golf", "Mental Mayhem", "Another")
      val slot3Places = slot3ActivityNames.map(name =>
        ActivityPlace(Activity(name, mappings(name)._1, mappings(name)._2, mappings(name)._3), slot3))
      val slot4ActivityNames = allActivityNames -- Set("Baking", "Fire Starter", "Video & Photography", "Indoor Games", "Another")
      val slot4Places = slot4ActivityNames.map(name =>
        ActivityPlace(Activity(name, mappings(name)._1, mappings(name)._2, mappings(name)._3), slot4))
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

    OverallPlan.randomBestOf(realDataStartPlan, 100,20)
  }

}