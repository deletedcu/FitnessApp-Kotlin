package com.liverowing.liverowing.model.pm
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcelable
import com.liverowing.liverowing.extensions.calcDistance
import com.liverowing.liverowing.extensions.calcTime
import com.liverowing.liverowing.extensions.calcWorkoutDurationDistance
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class RowingStatus(val elapsedTime: Float,
                        val distance: Float,
                        val workoutType: WorkoutType,
                        val intervalType: IntervalType,
                        val workoutState: WorkoutState,
                        val rowingState: RowingState,
                        val strokeState: StrokeState,
                        val workoutDuration: Float,
                        val workoutDurationType: DurationType,
                        val totalWorkDistance: Float,
                        val dragFactor: Int
) : Parcelable {
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
}
