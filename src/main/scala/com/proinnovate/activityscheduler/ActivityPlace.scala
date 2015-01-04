package com.proinnovate.activityscheduler

case class ActivityPlace(activity: Activity, slot: Slot) {
  override def toString = s"${activity.name}@${slot.name}"
}