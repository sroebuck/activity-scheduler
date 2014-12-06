package com.proinnovate.activityscheduler

import org.scalacheck.{Gen, Arbitrary}

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
class IndividualTest extends org.scalatest.FunSuite {

  test("Create a set of individuals") {

    val names = "Effie Goff,Jimmie Mastronardi,Sung Weyandt,Nelida Hyatt,Gabriel Gallup,Hyo Manns,Carla Minchew,Oretha Paul,Dortha Teeters,Sherman Drovin,Carolyne Schertz,Tanesha Casavant,Glennie Vanloan,Ben Ruder,Alicia Hilty,Bianca Paulus,Era Piekarski,Neomi Tapp,Fletcher Hearon,Nicolle Trinidad,Shayna Shultz,Karina Durazo,Regina Vick,Marsha Rhine,Nickole Prevatte,Tiffiny Cotner,Jesica Bent,Karlyn Hinman,Elba Curfman,Wade Boudreau".split(',').toSeq

    import ActivityRatingsTest.arbActivityRatings
    val individuals = names.map(Individual(_, arbActivityRatings.arbitrary.sample.get))

    println(individuals)

  }

}
