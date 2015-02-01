package com.proinnovate.activityscheduler

import java.io.{InputStreamReader, InputStream}

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}

import scala.util.Try

object ChoiceReader {

  def readActivitiesAndIndividuals(inputStream: InputStream): (Seq[String], Set[Individual]) = {
    implicit object TabDelimitedFormat extends DefaultCSVFormat {
      override val delimiter = '\t'
    }

    val reader: InputStreamReader = new InputStreamReader(inputStream)
    val csvReader: CSVReader = CSVReader.open(reader)
    val it: Iterator[Seq[String]] = csvReader.iterator
    val headings = it.next().map(s => s.replace("\n", " "))

    val activityHeadings = headings.tail.tail

    val individuals: Iterator[Individual] = for {
      entry <- it
    } yield {
      val name :: group :: ratings = entry
      val pairs = activityHeadings zip ratings
      val activityRatingMap = pairs.map {
        case (activity, rating) =>
          (activity, Try(rating.toInt).getOrElse(10))
      }
      val activityRatings = ActivityRatings(activityRatingMap.toMap)
      Individual(name, group, activityRatings)
    }
    val result = (activityHeadings, individuals.toSet)
    inputStream.close()
    result
  }

}
