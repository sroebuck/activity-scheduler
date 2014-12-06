package com.proinnovate.activityscheduler

/*
 * Copyright (c) Stuart Roebuck, 2014
 */
case class IndividualPlan(individual: Individual, activitySet: Set[ActivitySlot]) {

  lazy val fit = 0

}
