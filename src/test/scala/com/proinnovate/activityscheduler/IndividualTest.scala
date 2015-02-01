package com.proinnovate.activityscheduler

import org.scalacheck.{Gen, Arbitrary}

object IndividualTest {

  lazy val individualSet = {
    val names = "Effie Goff,Jimmie Mastronardi,Sung Weyandt,Nelida Hyatt,Gabriel Gallup,Hyo Manns,Carla Minchew,Oretha Paul,Dortha Teeters,Sherman Drovin,Carolyne Schertz,Tanesha Casavant,Glennie Vanloan,Ben Ruder,Alicia Hilty,Bianca Paulus,Era Piekarski,Neomi Tapp,Fletcher Hearon,Nicolle Trinidad,Shayna Shultz,Karina Durazo,Regina Vick,Marsha Rhine,Nickole Prevatte,Tiffiny Cotner,Jesica Bent,Karlyn Hinman,Elba Curfman,Wade Boudreau".split(',').toSet

    import ActivityRatingsTest.arbActivityRatings
    names.map(Individual(_, "a", arbActivityRatings.arbitrary.sample.get))
  }

}
