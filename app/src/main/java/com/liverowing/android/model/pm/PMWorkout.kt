package com.liverowing.android.model.pm

class PMWorkout(val workoutType: WorkoutType) {
    companion object {
        const val PM_MAX_INTERVALS = 30
    }

    var workoutDuration: Int = 0
    var splitDuration: Int = 0
    var targetPace: Int? = null
    var restDuration: Int = 0
    var intervals = mutableListOf<Interval>()

    fun setFixedWorkout(wDuration: Int, sDuration: Int, pace: Int?): Boolean {
        var status = false

        // Perform type and range checking
        when (workoutType) {
            WorkoutType.FIXEDTIME_NOSPLITS,
            WorkoutType.FIXEDTIME_SPLITS -> {
                if ((isWorkDurationInRangeForTimeWorkout(wDuration)) && (isSplitDurationInRangeForTimeWorkout(wDuration, sDuration))) {
                    status = true
                }
            }

            WorkoutType.FIXEDDIST_NOSPLITS,
            WorkoutType.FIXEDDIST_SPLITS -> {
                if ((isWorkDurationInRangeForDistanceWorkout(wDuration)) && (isSplitDurationInRangeForDistanceWorkout(wDuration, sDuration))) {
                    status = true
                }
            }

            WorkoutType.FIXED_CALORIE,
            WorkoutType.FIXED_WATTMINUTES -> {
                if ((isDurationInRangeForCalorieWattWorkout(wDuration)) && (isSplitDurationInRangeForDistanceWorkout(wDuration, sDuration))) {
                    status = true
                }
            }
        }

        if (status) {
            workoutDuration = wDuration
            splitDuration = sDuration
            targetPace = pace
        }

        return status
    }

    fun setFixedIntervalWorkout(wDuration: Int, rDuration: Int, pace: Int): Boolean {
        var status = false

        // Perform range checking
        when (workoutType) {
            WorkoutType.FIXEDTIME_INTERVAL -> {
                if ((isWorkDurationInRangeForTimeWorkout(wDuration)) && (isRestDurationInRangeForWorkout(rDuration))) {
                    status = true
                }
            }

            WorkoutType.FIXEDDIST_INTERVAL -> {
                if ((isWorkDurationInRangeForDistanceWorkout(wDuration)) && (isRestDurationInRangeForWorkout(rDuration))) {
                    status = true
                }
            }
        }

        if (status) {
            workoutDuration = wDuration
            restDuration = rDuration
            targetPace = pace
        }

        return status
    }

    fun addInterval(intervalType: IntervalType, workoutDuration: Int, rDuration: Int, pace: Int?): Boolean {
        var status = false
        val wDuration = workoutDuration

        if ((workoutType == WorkoutType.VARIABLE_INTERVAL) || (workoutType == WorkoutType.VARIABLE_UNDEFINEDREST_INTERVAL)) {
            if (intervals.size < PM_MAX_INTERVALS) {
                // Perform range checking
                when (intervalType) {
                    IntervalType.TIME,
                    IntervalType.TIMERESTUNDEFINED -> {
                        if ((isWorkDurationInRangeForTimeWorkout(workoutDuration)) && (isRestDurationInRangeForWorkout(rDuration))) {
                            status = true
                        }
                    }

                    else -> {
                        if ((isWorkDurationInRangeForDistanceWorkout(workoutDuration)) && (isRestDurationInRangeForWorkout(rDuration))) {
                            status = true
                        }
                    }
                }

                if (status) {
                    intervals.add(Interval(intervalType, wDuration, rDuration, pace))
                }
            }
        }

        return status
    }

    private fun isWorkDurationInRangeForTimeWorkout(timeDuration: Int): Boolean {
        var status = false

        if ((timeDuration >= 20) && (timeDuration < 36000)) {
            status = true
        }

        return status
    }

    private fun isWorkDurationInRangeForDistanceWorkout(distanceDuration: Int): Boolean {
        var status = false

        if ((distanceDuration >= 100) && (distanceDuration < 50000)) {
            status = true
        }

        return status
    }

    private fun isDurationInRangeForCalorieWattWorkout(calorieWattDuration: Int): Boolean {
        var status = false

        if ((calorieWattDuration >= 1) && (calorieWattDuration < 65534)) {
            status = true
        }

        return status
    }

    private fun isSplitDurationInRangeForTimeWorkout(timeDuration: Int, splitDuration: Int): Boolean {
        var status = false

        if (((splitDuration >= 20) && (splitDuration <= timeDuration)) || (splitDuration == 0)) {
            if (splitDuration > 0 && timeDuration > 0) {
                // The minimum split duration must not cause the total number of splits per workout to exceed 30.
                if ((timeDuration / splitDuration) <= 30) {
                    status = true
                }
            } else {
                status = true
            }
        }

        return status
    }

    private fun isSplitDurationInRangeForDistanceWorkout(distanceDuration: Int, splitDuration: Int): Boolean {
        var status = false

        if (((splitDuration >= 100) &&
                (splitDuration <= distanceDuration)) ||
                (splitDuration == 0)) {
            if (splitDuration > 0 && distanceDuration > 0) {
                // The minimum split duration must not cause the total number of splits per workout to exceed 30.
                if ((distanceDuration / splitDuration) <= 30) {
                    status = true
                }
            } else {
                status = true
            }
        }

        return status
    }

    private fun isRestDurationInRangeForWorkout(restDuration: Int): Boolean {
        var status = false

        if (restDuration <= 595) {
            status = true
        }

        return status
    }

    data class Interval(val type: IntervalType, val workoutDuration: Int, val restDuration: Int, val targetPace: Int?)
}