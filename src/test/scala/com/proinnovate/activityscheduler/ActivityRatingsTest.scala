package com.proinnovate.activityscheduler

import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
class ActivityRatingsTest extends FunSuite with PropertyChecks {

  import ActivityRatingsTest.arbActivityRatings

  test("Can create some ratings") {
    forAll {
      (ratings: ActivityRatings) =>
        assert(ratings.ratingsMap.size == ActivitySlot.universalNameSet.size)
    }
  }

}

object ActivityRatingsTest {

  type Rating = Int

  implicit lazy val arbActivityRating: Arbitrary[Rating] = Arbitrary(Gen.chooseNum(1,10))

  implicit lazy val arbActivityRatings: Arbitrary[ActivityRatings] = Arbitrary {
    val ratings = for {
      name <- ActivitySlot.universalNameSet
      rating <- arbActivityRating.arbitrary.sample
    } yield (name, rating)
    ActivityRatings(ratings.toMap)
  }

}
