package com.proinnovate.activityscheduler

import java.io.{InputStreamReader, InputStream}

import com.github.tototoshi.csv.{DefaultCSVFormat, CSVReader}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{DiagrammedAssertions, FunSuite}

import scala.util.Try

class ChoiceReaderTest extends FunSuite with LazyLogging with DiagrammedAssertions {

  test("Read in choices correctly") {
    val inputStream: InputStream = getClass.getResourceAsStream("activity-choices.csv")
    val (activityHeadings, individuals) = ChoiceReader.readActivitiesAndIndividuals(inputStream)
    val expectedHeadings = Seq("Archery", "Trail Biking", "Ropes Course", "Tree Climb", "High Ropes",
      "Adventure Golf", "Pitch & Putt", "Orienteering", "Games Hall", "Football", "Adventure Playground")
    assert(activityHeadings == expectedHeadings)
  }

}
