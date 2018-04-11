package com.liverowing.liverowing.model.pm
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcelable
import com.liverowing.liverowing.extensions.calcDistance
import com.liverowing.liverowing.extensions.calcTime
import com.liverowing.liverowing.extensions.calcWorkoutDurationDistance
import kotlinx.android.parcel.Parcelize
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
        fun fromCharacteristic(data: BluetoothGattCharacteristic) : RowingStatus {
            val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8

            val time = data.calcTime(0)
            val distance = data.calcDistance(3)
            val workoutType = WorkoutType.fromInt(data.getIntValue(uint8, 6))
            val intervalType = IntervalType.fromInt(data.getIntValue(uint8, 7))
            val workoutState = WorkoutState.fromInt(data.getIntValue(uint8, 8))
            val rowingState = RowingState.fromInt(data.getIntValue(uint8, 9))
            val strokeState = StrokeState.fromInt(data.getIntValue(uint8, 10))
            val workoutDurationType = DurationType.fromInt(data.getIntValue(uint8, 17))
            val workoutDuration = if (workoutDurationType == DurationType.TIME) data.calcTime(14) else data.calcWorkoutDurationDistance(14)
            val totalWorkDistance = data.calcWorkoutDurationDistance(14)
            val dragFactor = data.getIntValue(uint8, 18)

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
