package com.liverowing.android.model.parse

import com.parse.*
import java.util.*

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
@ParseClassName("Stats")
class Stats : ParseObject() {
    var caloriesBurned by ParseDelegate<Int?>()
    var experience by ParseDelegate<Int?>()
    var metersRowed by ParseDelegate<Int?>()
    var wins by ParseDelegate<Int?>()
    var losses by ParseDelegate<Int?>()
    var level by ParseDelegate<Int?>()
    var toNextLevel by ParseDelegate<Int?>()
    var completedWorkouts by ParseDelegate<Int?>()
    var completedGoalProcess by ParseDelegate<Int?>()
    var completedGoals by ParseDelegate<List<Goals>?>()
    var goalStartDate by ParseDelegate<Date?>()
    var startDate by ParseDelegate<Date?>()
    var goalProgress by ParseDelegate<Int?>()
}