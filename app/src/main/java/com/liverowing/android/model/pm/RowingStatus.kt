package com.liverowing.android.model.pm
import com.liverowing.android.extensions.*
import kotlin.math.floor

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class RowingStatus(val elapsedTime: Double,
                        val distance: Double,
                        val workoutType: WorkoutType,
                        val intervalType: IntervalType,
                        val workoutState: WorkoutState,
                        val rowingState: RowingState,
                        val strokeState: StrokeState,
                        val workoutDuration: Double,
                        val workoutDurationType: DurationType,
                        val totalWorkDistance: Double,
                        val dragFactor: Int
) {
    companion object {
        fun fromByteArray(data: ByteArray) : RowingStatus {
            val time = data.calcTime(0)
            val distance = data.calcDistance(3)
            val workoutType = WorkoutType.fromInt(data.getIntValue(FORMAT_UINT8, 6))
            val intervalType = IntervalType.fromInt(data.getIntValue(FORMAT_UINT8, 7))
            val workoutState = WorkoutState.fromInt(data.getIntValue(FORMAT_UINT8, 8))
            val rowingState = RowingState.fromInt(data.getIntValue(FORMAT_UINT8, 9))
            val strokeState = StrokeState.fromInt(data.getIntValue(FORMAT_UINT8, 10))
            val workoutDurationType = DurationType.fromInt(data.getIntValue(FORMAT_UINT8, 17))
            val workoutDuration = if (workoutDurationType == DurationType.TIME) data.calcTime(14) else data.calcWorkoutDurationDistance(14)
            val totalWorkDistance = data.calcWorkoutDurationDistance(14)
            val dragFactor = data.getIntValue(FORMAT_UINT8, 18)

            return RowingStatus(
                    time, distance, workoutType, intervalType, workoutState, rowingState,
                    strokeState, workoutDuration, workoutDurationType, totalWorkDistance, dragFactor
            )
        }
    }

    fun durationLeftOnSplit(splitSize: Int = 0): Double {
        if (splitSize == 0) {
            return when (intervalType) {
                IntervalType.TIME,
                IntervalType.TIMERESTUNDEFINED -> workoutDuration - elapsedTime

                IntervalType.DIST,
                IntervalType.DISTANCERESTUNDEFINED -> workoutDuration - distance

                else -> 0.0
            }
        } else {
            return when (intervalType) {
                IntervalType.TIME,
                IntervalType.TIMERESTUNDEFINED -> {
                    val currentSplit = floor(elapsedTime / splitSize)
                    (splitSize * (currentSplit+1)) - elapsedTime
                }

                IntervalType.DIST,
                IntervalType.DISTANCERESTUNDEFINED -> {
                    val currentSplit = floor(distance / splitSize)
                    (splitSize * (currentSplit+1)) - distance
                }

                else -> 0.0
            }
        }
    }

    fun currentSplitSize(splitSize: Int = 0): Double {
        return if (splitSize == 0) {
            workoutDuration
        } else {
            val currentSplit: Double = when (intervalType) {
                IntervalType.TIME,
                IntervalType.TIMERESTUNDEFINED -> floor(elapsedTime / splitSize)

                IntervalType.DIST,
                IntervalType.DISTANCERESTUNDEFINED -> floor(distance / splitSize)

                else -> 1.0
            }

            if (currentSplit * splitSize > workoutDuration) workoutDuration - (currentSplit * splitSize) else splitSize.toDouble()
        }
    }
}
