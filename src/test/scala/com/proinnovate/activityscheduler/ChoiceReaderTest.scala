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
    val expectedHeadings = Seq("Group", "Archery", "Trail Biking", "Ropes Course", "Tree Climb (3 slots)",
      "High Ropes", "Adventure Golf", "Baking", "Crafts", "Creative Art (2 slots)",
      "Video / Photography Workshop (2 slots)", "Wacky Science (2 slots)", "Indoor Games", "Games Hall", "Football",
      "Adventure Playground")
    assert(activityHeadings == expectedHeadings)
  }

}
