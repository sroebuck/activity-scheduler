package com.proinnovate.activityscheduler

import java.io.{InputStreamReader, InputStream}

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}

import scala.util.Try

object ChoiceReader {

  def readActivitiesAndIndividuals(inputStream: InputStream): (Seq[String], Seq[Individual]) = {
    implicit object TabDelimitedFormat extends DefaultCSVFormat {
      override val delimiter = '\t'
    }

    val reader: InputStreamReader = new InputStreamReader(inputStream)
    val csvReader: CSVReader = CSVReader.open(reader)
    val it: Iterator[Seq[String]] = csvReader.iterator
    val headings = it.next().map(s => s.replace("\n", " "))

    val expectedHeadings = Seq("Name", "Group", "Archery", "Trail Biking", "Ropes Course", "Tree Climb (3 slots)",
      "High Ropes", "Adventure Golf", "Baking", "Crafts", "Creative Art (2 slots)",
      "Video / Photography Workshop (2 slots)", "Wacky Science (2 slots)", "Indoor Games", "Games Hall", "Football",
      "Adventure Playground")
    assert(headings == expectedHeadings)
    val activityHeadings = headings.tail

    val individuals: Iterator[Individual] = for {
      entry <- it
    } yield {
      val name :: ratings = entry
      val pairs = activityHeadings zip ratings
      val activityRatingMap = pairs.map {
        case (activity, rating) =>
          (activity, Try(rating.toInt).getOrElse(10))
      }
      val activityRatings = ActivityRatings(activityRatingMap.toMap)
      Individual(name, activityRatings)
    }
    inputStream.close()
    (activityHeadings, individuals.toSeq)
  }

}
