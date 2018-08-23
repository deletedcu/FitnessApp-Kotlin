package com.liverowing.android.pm.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.liverowing.android.model.messages.*
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.model.parse.WorkoutType.Companion.REST_TYPE_NORMAL
import com.liverowing.android.model.parse.WorkoutType.Companion.REST_TYPE_VARIABLE
import com.liverowing.android.model.parse.WorkoutType.Companion.SEGMENT_VALUE_TYPE_DISTANCE
import com.liverowing.android.model.parse.WorkoutType.Companion.SEGMENT_VALUE_TYPE_TIMED
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_CUSTOM
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_DISTANCE
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_TIMED
import com.liverowing.android.model.pm.IntervalType
import com.liverowing.android.model.pm.PMWorkout
import com.liverowing.android.model.pm.WorkoutType.*
import com.liverowing.android.pm.PMDevice
import com.liverowing.android.util.csafe.*
import no.nordicsemi.android.ble.BleManagerCallbacks
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.callback.SuccessCallback
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class PM5BleDevice(private val ctx: Context, private val device: BluetoothDevice) : PMDevice, BleManagerCallbacks {
    private val eventBus = EventBus.getDefault()
    private val manager = PM5Manager(ctx)

    override fun connect() {
        if (manager.isConnected) {
            manager.disconnect().enqueue()
        }

        manager.setGattCallbacks(this@PM5BleDevice)
        manager.connect(device).enqueue()
    }

    override fun disconnect() {
        manager.disconnect()
    }

    override fun programWorkout(workoutType: WorkoutType, targetPace: Int?) {
        val workout = when (workoutType.valueType) {
            VALUE_TYPE_DISTANCE -> PMWorkout(FIXEDDIST_SPLITS)
            VALUE_TYPE_TIMED -> PMWorkout(FIXEDTIME_SPLITS)
            VALUE_TYPE_CUSTOM -> PMWorkout(VARIABLE_INTERVAL)
            else -> PMWorkout(JUSTROW_SPLITS)
        }

        when (workoutType.valueType) {
            VALUE_TYPE_DISTANCE -> workout.setFixedWorkout(workoutType.value, workoutType.calculatedSplitLength, targetPace)
            VALUE_TYPE_TIMED -> workout.setFixedWorkout(workoutType.value, workoutType.calculatedSplitLength, targetPace)

            VALUE_TYPE_CUSTOM -> {
                for (segment in workoutType.segments!!) {
                    when (segment.valueType) {
                        SEGMENT_VALUE_TYPE_TIMED -> {
                            when (segment.restType) {
                                REST_TYPE_NORMAL -> workout.addInterval(IntervalType.TIME, segment.value!!, segment.restValue!!, targetPace)
                                REST_TYPE_VARIABLE -> workout.addInterval(IntervalType.TIMERESTUNDEFINED, segment.value!!, segment.restValue!!, targetPace)
                                else -> Timber.d("** Invalid restType (Timed): %s", segment.restType.toString())
                            }
                        }

                        SEGMENT_VALUE_TYPE_DISTANCE -> {
                            when (segment.restType) {
                                REST_TYPE_NORMAL -> workout.addInterval(IntervalType.DIST, segment.value!!, segment.restValue!!, targetPace)
                                REST_TYPE_VARIABLE -> workout.addInterval(IntervalType.DISTANCERESTUNDEFINED, segment.value!!, segment.restValue!!, targetPace)
                                else -> Timber.d("** Invalid restType (Distance): %s", segment.restType.toString())
                            }
                        }

                        else -> Timber.d("** Invalid valueType: %s", segment.valueType.toString())
                    }
                }
            }
        }

        configureWorkout(workout)
    }

    private fun configureWorkout(workout: PMWorkout) {
        // Check for special case variable interval workout, where the number of intervals is greater than PM_MAX_INTERVALS_PER_CSAFE_MSG.
        // If this is the case we need to setup this workout using multiple commands due to the PM communications interface size limit.
        if ((workout.workoutType == VARIABLE_INTERVAL || workout.workoutType == VARIABLE_UNDEFINEDREST_INTERVAL) && workout.intervals.size > PM_MAX_INTERVALS_PER_CSAFE_MSG) {
            configureVariableIntervalWorkout(workout)
        } else {
            val message = when (workout.workoutType) {
                JUSTROW_SPLITS -> createJustRowWorkoutMessage(workout)

                FIXEDDIST_SPLITS,
                FIXEDTIME_SPLITS,
                FIXED_CALORIE,
                FIXED_WATTMINUTES -> createFixedWorkoutMessage(workout)

                FIXEDTIME_INTERVAL,
                FIXEDDIST_INTERVAL -> createFixedIntervalWorkoutMessage(workout)

                VARIABLE_INTERVAL,
                VARIABLE_UNDEFINEDREST_INTERVAL -> createVariableIntervalWorkoutMessage(workout, 0, workout.intervals.size - 1)

                else -> throw Exception("Invalid workout type.")
            }

            manager.queueCommands(message, FailCallback { _, _ -> EventBus.getDefault().post(WorkoutProgrammed(false)) }, SuccessCallback { EventBus.getDefault().post(WorkoutProgrammed(true)) })
        }

    }

    private fun createJustRowWorkoutMessage(workout: PMWorkout): ByteArray {
        val frame = Frame()
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTTYPE, listOf(workout.workoutType.value)))
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_CONFIGURE_WORKOUT, listOf(1)))
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_SCREENSTATE, listOf(SCREENTYPE_WORKOUT, SCREENVALUEWORKOUT_PREPARETOROWWORKOUT)))
        return frame.formattedFrame()
    }

    private fun createFixedWorkoutMessage(workout: PMWorkout): ByteArray {
        var wDuration = workout.workoutDuration
        var sDuration = workout.splitDuration

        val type = when (workout.workoutType) {
            FIXEDDIST_NOSPLITS,
            FIXEDDIST_SPLITS,
            FIXED_CALORIE,
            FIXED_WATTMINUTES -> PM_DISTANCE_TYPE

            FIXEDTIME_NOSPLITS,
            FIXEDTIME_SPLITS -> {
                wDuration *= 100
                sDuration *= 100
                PM_TIME_TYPE
            }

            else -> throw Exception("Invalid workout type.")
        }

        val frame = Frame()
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTTYPE, listOf(workout.workoutType.value)))
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTDURATION, listOf(type, wDuration shr 24 and 0xFF, wDuration shr 16 and 0xFF, wDuration shr 8 and 0xFF, wDuration and 0xFF)))
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_SPLITDURATION, listOf(type, sDuration shr 24 and 0xFF, sDuration shr 16 and 0xFF, sDuration shr 8 and 0xFF, sDuration and 0xFF)))
        if (workout.targetPace != null) {
            // Convert target pace to UInt32, .01 resolution
            val pace = workout.targetPace!! * 100
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_TARGETPACETIME, listOf(pace shr 24 and 0xFF, pace shr 16 and 0xFF, pace shr 8 and 0xFF, pace and 0xFF)))
        }
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_CONFIGURE_WORKOUT, listOf(1)))
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_SCREENSTATE, listOf(SCREENTYPE_WORKOUT, SCREENVALUEWORKOUT_PREPARETOROWWORKOUT)))
        return frame.formattedFrame()
    }

    private fun createFixedIntervalWorkoutMessage(workout: PMWorkout): ByteArray {
        val frame = Frame()
        return frame.formattedFrame()
    }

    private fun createVariableIntervalWorkoutMessage(workout: PMWorkout, start: Int, end: Int): ByteArray {
        val frame = Frame()

        for (i in start..end) {
            val interval = workout.intervals[i]
            var wDuration = interval.workoutDuration

            val type = when (interval.type) {
                IntervalType.TIME,
                IntervalType.TIMERESTUNDEFINED -> {
                    wDuration *= 100
                    PM_TIME_TYPE
                }

                else -> PM_DISTANCE_TYPE
            }

            Timber.d("** Programming: Set interval count to $i")
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTINTERVALCOUNT, listOf(i)))

            if (i == 0) {
                // Set Workout Type for first interval only
                Timber.d("** Programming: Setting workout type to {$workout.workoutType.value}")
                frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTTYPE, listOf(workout.workoutType.value)))
            }

            Timber.d("** Programming: Setting interval type, duration and rest")
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_INTERVALTYPE, listOf(interval.type.value)))
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTDURATION, listOf(type, wDuration shr 24 and 0xFF, wDuration shr 16 and 0xFF, wDuration shr 8 and 0xFF, wDuration and 0xFF)))
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_RESTDURATION, listOf(interval.restDuration shr 8 and 0xFF, interval.restDuration and 0xFF)))

            if (interval.targetPace != null) {
                Timber.d("** Programming: Settings target pace to ${interval.targetPace}")
                val pace = interval.targetPace * 100
                frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_TARGETPACETIME, listOf(pace shr 24 and 0xFF, pace shr 16 and 0xFF, pace shr 8 and 0xFF, pace and 0xFF)))
            }

            Timber.d("** Programming: Configure workout")
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_CONFIGURE_WORKOUT, listOf(1)))

            // If last interval configured go to the rowing screen
            if (i == workout.intervals.size - 1) {
                Timber.d("** Programming: Set screen")
                frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_SCREENSTATE, listOf(SCREENTYPE_WORKOUT, SCREENVALUEWORKOUT_PREPARETOROWWORKOUT)))
            }
        }

        return frame.formattedFrame()
    }

    private fun configureVariableIntervalWorkout(workout: PMWorkout) {
        val end = workout.intervals.size - 1
        for (i in 0..end step 2) {
            val message = createVariableIntervalWorkoutMessage(workout, i, minOf(i + 1, end))
            if (i <= end - 1) {
                manager.queueCommands(message, FailCallback { _, _ -> EventBus.getDefault().post(WorkoutProgrammed(false)) }, SuccessCallback { EventBus.getDefault().post(WorkoutProgrammed(true)) })
            } else {
                manager.queueCommands(message, FailCallback { _, _ -> EventBus.getDefault().post(WorkoutProgrammed(false)) })
            }
        }
    }

    override fun onDeviceConnecting(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.post(DeviceConnecting(device))
        }
    }

    override fun onDeviceConnected(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.removeStickyEvent(DeviceDisconnected::class.java)
            eventBus.postSticky(DeviceConnected(device))
        }
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.post(DeviceDisconnecting(device))
        }
    }

    override fun onDeviceDisconnected(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.removeStickyEvent(DeviceReady::class.java)
            eventBus.removeStickyEvent(DeviceConnected::class.java)
            eventBus.postSticky(DeviceDisconnected(device))
        }
    }

    override fun onDeviceReady(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.postSticky(DeviceReady(device, device.name))
        }
    }

    override fun onDeviceNotSupported(device: BluetoothDevice?) {
        Timber.d("** ** onDeviceNotSupported")
    }

    override fun onError(device: BluetoothDevice?, message: String?, errorCode: Int) {
        Timber.d("** ** onError")
    }

    override fun onBondingFailed(device: BluetoothDevice?) {
        Timber.d("** ** onBondingFailed")
    }

    override fun onServicesDiscovered(device: BluetoothDevice?, optionalServicesFound: Boolean) {
        Timber.d("** ** onServicesDiscovered")
    }

    override fun onBondingRequired(device: BluetoothDevice?) {
        Timber.d("** ** onBondingRequired")
    }

    override fun onLinkLossOccurred(device: BluetoothDevice?) {
        Timber.d("** ** onLinkLossOccurred")
    }

    override fun onBonded(device: BluetoothDevice?) {
        Timber.d("** ** onBonded")
    }
}