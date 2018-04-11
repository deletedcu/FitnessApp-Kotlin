package com.liverowing.liverowing.service.device

import android.bluetooth.*
import android.content.Context
import android.util.Log
import com.liverowing.liverowing.LiveRowing
import com.liverowing.liverowing.csafe.Frame
import com.liverowing.liverowing.model.parse.WorkoutType
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.REST_TYPE_NORMAL
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.REST_TYPE_VARIABLE
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.SEGMENT_VALUE_TYPE_DISTANCE
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.SEGMENT_VALUE_TYPE_TIMED
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.VALUE_TYPE_CUSTOM
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.VALUE_TYPE_DISTANCE
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.VALUE_TYPE_TIMED
import com.liverowing.liverowing.model.pm.*
import com.liverowing.liverowing.model.pm.WorkoutType.*
import com.liverowing.liverowing.service.messages.DeviceConnected
import com.liverowing.liverowing.service.messages.DeviceDisconnected
import com.liverowing.liverowing.service.messages.WorkoutProgrammed
import com.liverowing.liverowing.service.operations.GattOperation
import com.liverowing.liverowing.service.operations.GattReadCharacteristicOperation
import com.liverowing.liverowing.service.operations.GattSetNotificationOperation
import com.liverowing.liverowing.service.operations.GattWriteCharacteristicOperation
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by henrikmalmberg on 2017-12-17.
 */
class BleDevice(val context: Context, val device: BluetoothDevice) : Device() {
    companion object {
        // Peripheral UUID
        const val PM_DEVICE_UUID_STRING = "ce060000-43e5-11e4-916c-0800200c9a66"

        // Service UUIDs
        const val PM_DEVICEINFO_SERVICE_UUID_STRING = "ce060010-43e5-11e4-916c-0800200c9a66"
        const val PM_CONTROL_SERVICE_UUID_STRING = "ce060020-43e5-11e4-916c-0800200c9a66"
        const val PM_ROWING_SERVICE_UUID_STRING = "ce060030-43e5-11e4-916c-0800200c9a66"

        // Characteristic UUIDs for PM device info service
        const val MODEL_NUMBER_CHARACTERISIC_UUID_STRING = "ce060011-43e5-11e4-916c-0800200c9a66"
        const val SERIAL_NUMBER_CHARACTERISTIC_UUID_STRING = "ce060012-43e5-11e4-916c-0800200c9a66"
        const val HWREVISION_CHARACTERISIC_UUID_STRING = "ce060013-43e5-11e4-916c-0800200c9a66"
        const val FWREVISION_CHARACTERISIC_UUID_STRING = "ce060014-43e5-11e4-916c-0800200c9a66"
        const val MANUFNAME_CHARACTERISIC_UUID_STRING = "ce060015-43e5-11e4-916c-0800200c9a66"
        const val MACHINE_TYPE_CHARACTERISIC_UUID_STRING = "ce060016-43e5-11e4-916c-0800200c9a66"

        // Characteristic UUIDs for PM control service
        const val TRANSMIT_TO_PM_CHARACTERISIC_UUID_STRING = "ce060021-43e5-11e4-916c-0800200c9a66"
        const val RECEIVE_FROM_PM_CHARACTERISIC_UUID_STRING = "ce060022-43e5-11e4-916c-0800200c9a66"

        // Characteristic UUIDs for rowing service
        const val ROWING_STATUS_CHARACTERISIC_UUID_STRING = "ce060031-43e5-11e4-916c-0800200c9a66"
        const val EXTRASTATUS1_CHARACTERISIC_UUID_STRING = "ce060032-43e5-11e4-916c-0800200c9a66"
        const val EXTRASTATUS2_CHARACTERISIC_UUID_STRING = "ce060033-43e5-11e4-916c-0800200c9a66"
        const val ROWINGSTATUS_SAMPLERATE_CHARACTERISIC_UUID_STRING = "ce060034-43e5-11e4-916c-0800200c9a66"
        const val STROKEDATA_CHARACTERISIC_UUID_STRING = "ce060035-43e5-11e4-916c-0800200c9a66"
        const val EXTRA_STROKEDATA_CHARACTERISIC_UUID_STRING = "ce060036-43e5-11e4-916c-0800200c9a66"
        const val SPLITINTERVAL_DATA_CHARACTERISIC_UUID_STRING = "ce060037-43e5-11e4-916c-0800200c9a66"
        const val EXTRA_SPLITINTERVAL_DATA_CHARACTERISIC_UUID_STRING = "ce060038-43e5-11e4-916c-0800200c9a66"
        const val ROWING_SUMMARY_CHARACTERISIC_UUID_STRING = "ce060039-43e5-11e4-916c-0800200c9a66"
        const val EXTRA_ROWING_SUMMARY_CHARACTERISIC_UUID_STRING = "ce06003a-43e5-11e4-916c-0800200c9a66"
        const val HEARTRATE_BELTINFO_CHARACTERISIC_UUID_STRING = "ce06003b-43e5-11e4-916c-0800200c9a66"
        const val FORCE_CURVE_CHARACTERISIC_UUID_STRING = "ce06003d-43e5-11e4-916c-0800200c9a66"

        const val PM_MAX_INTERVALS_PER_CSAFE_MSG = 2
    }

    private val rowingServiceUUID = UUID.fromString(PM_ROWING_SERVICE_UUID_STRING)
    private val controlServiceUUID = UUID.fromString(PM_CONTROL_SERVICE_UUID_STRING)
    private val controlServiceTransmitCharacteristicUUID = UUID.fromString(TRANSMIT_TO_PM_CHARACTERISIC_UUID_STRING)
    private val controlServiceReceiveCharacteristicUUID = UUID.fromString(RECEIVE_FROM_PM_CHARACTERISIC_UUID_STRING)

    private lateinit var mGatt: BluetoothGatt
    private val operationQueue = LinkedList<GattOperation>()

    override lateinit var name: String
    override var connected: Boolean = false

    override fun connect() {
        if (device.bondState == BluetoothDevice.BOND_BONDED) {
            unPairDevice(device)
        }

        name = device.name
        mGatt = device.connectGatt(context, false, GattClientCallback(device))
    }

    override fun disconnect() {
        TODO("not implemented")
    }

    override fun setupWorkout(workoutType: WorkoutType, targetPace: Int?) {
        val workout = when (workoutType.valueType) {
            VALUE_TYPE_DISTANCE -> PMWorkout(FIXEDDIST_SPLITS)
            VALUE_TYPE_TIMED -> PMWorkout(FIXEDTIME_SPLITS)
            VALUE_TYPE_CUSTOM -> {
                Log.d("LiveRowing", "CUSTOM WORKOUT"); PMWorkout(VARIABLE_INTERVAL)
            }
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
                                else -> Log.d("LiveRowing", "Invalid restType (Timed): " + segment.restType.toString())
                            }
                        }

                        SEGMENT_VALUE_TYPE_DISTANCE -> {
                            when (segment.restType) {
                                REST_TYPE_NORMAL -> workout.addInterval(IntervalType.DIST, segment.value!!, segment.restValue!!, targetPace)
                                REST_TYPE_VARIABLE -> workout.addInterval(IntervalType.DISTANCERESTUNDEFINED, segment.value!!, segment.restValue!!, targetPace)
                                else -> Log.d("LiveRowing", "Invalid restType (Distance): " + segment.restType.toString())
                            }
                        }

                        else -> Log.d("LiveRowing", "Invalid valueType: " + segment.valueType.toString())
                    }
                }
            }
        }

        configureWorkout(workout)
    }

    private fun configureWorkout(workout: PMWorkout) {
        if (LiveRowing.workoutState != WorkoutState.WAITTOBEGIN) {
            // We need to terminate a running workout before continuing
            terminateWorkout()
        }

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

                JUSTROW_NOSPLITS,
                FIXEDDIST_NOSPLITS,
                FIXEDTIME_NOSPLITS -> throw Exception("Invalid workout type.")
            }

            sendCsafeCommand(message, { EventBus.getDefault().post(WorkoutProgrammed(true)) })
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

            Log.d("LiveRowing", "Programming: Set interval count to $i")
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTINTERVALCOUNT, listOf(i)))

            if (i == 0) {
                // Set Workout Type for first interval only
                Log.d("LiveRowing", "Programming: Setting workout type to {$workout.workoutType.value}")
                frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTTYPE, listOf(workout.workoutType.value)))
            }

            Log.d("LiveRowing", "Programming: Setting interval type, duration and rest")
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_INTERVALTYPE, listOf(interval.type.value)))
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTDURATION, listOf(type, wDuration shr 24 and 0xFF, wDuration shr 16 and 0xFF, wDuration shr 8 and 0xFF, wDuration and 0xFF)))
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_RESTDURATION, listOf(interval.restDuration shr 8 and 0xFF, interval.restDuration and 0xFF)))

            if (interval.targetPace != null) {
                Log.d("LiveRowing", "Programming: Settings target pace to ${interval.targetPace}")
                val pace = interval.targetPace * 100
                frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_TARGETPACETIME, listOf(pace shr 24 and 0xFF, pace shr 16 and 0xFF, pace shr 8 and 0xFF, pace and 0xFF)))
            }

            Log.d("LiveRowing", "Programming: Configure workout")
            frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_CONFIGURE_WORKOUT, listOf(1)))

            // If last interval configured go to the rowing screen
            if (i == workout.intervals.size - 1) {
                Log.d("LiveRowing", "Programming: Set screen")
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
                sendCsafeCommand(message, { EventBus.getDefault().post(WorkoutProgrammed(true)) })
            } else {
                sendCsafeCommand(message, {})
            }
        }
    }

    private fun terminateWorkout() {
        Log.d("LiveRowing", "Must terminate workout first!")
        val frame = Frame()
        frame.addCommand(Frame.Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_SCREENSTATE, listOf(SCREENTYPE_WORKOUT, SCREENVALUEWORKOUT_TERMINATEWORKOUT)))
        sendCsafeCommand(frame.formattedFrame(), {})
    }

    private fun unPairDevice(device: BluetoothDevice) {
        val btClass = Class.forName("android.bluetooth.BluetoothDevice")
        val removeBondMethod = btClass.getMethod("removeBond")
        removeBondMethod.invoke(device)
    }

    private fun addOperation(operation: GattOperation) {
        operationQueue.add(operation)
        if (operationQueue.size == 1) {
            operation.execute(mGatt)
        }
    }

    private fun sendCsafeCommand(csafe: ByteArray, callback: () -> Unit?) {
        Log.d("LiveRowing", csafe.contentToString())

        var i = 0
        while (i <= csafe.size - 1) {
            val bytes = csafe.slice(IntRange(i, Math.min(i + 19, csafe.size - 1))).toByteArray()
            i += bytes.size

            addOperation(
                    GattWriteCharacteristicOperation(
                            device,
                            controlServiceUUID,
                            controlServiceTransmitCharacteristicUUID,
                            bytes
                    )
            )
        }

        addOperation(
                GattReadCharacteristicOperation(
                        device,
                        controlServiceUUID,
                        controlServiceReceiveCharacteristicUUID,
                        { callback() }
                )
        )
    }

    private inner class GattClientCallback(device: BluetoothDevice) : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.d("LiveRowing", "onConnectionStateChange newState: " + newState)

            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.e("LiveRowing", "Connection Gatt failure status " + status)
                connected = false
                //disconnectGattServer()
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                // handle anything not SUCCESS as failure
                Log.e("LiveRowing", "Connection not GATT success status " + status)
                connected = false
                //disconnectGattServer()
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("LiveRowing", "Connected to device " + gatt.device.address)
                connected = true
                mGatt = gatt
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("LiveRowing", "Disconnected from device")

                connected = false
            }

            if (!connected) {
                operationQueue.clear()
                EventBus.getDefault().apply {
                    removeStickyEvent(DeviceConnected(this@BleDevice))
                    post(DeviceDisconnected(this@BleDevice))
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing", "device service discovery unsuccessful, status " + status)
                return
            }

            Log.d("LiveRowing", "Initializing: setting write type and enabling notification for rowing service")
            val rowingService = gatt.getService(rowingServiceUUID)
            rowingService.characteristics
                    .filter { it.descriptors.size > 0 }
                    .forEach { addOperation(GattSetNotificationOperation(device, rowingService.uuid, it.uuid, it.descriptors[0].uuid)) }

            val controlService = gatt.getService(controlServiceUUID)
            controlService.characteristics
                    .filter { it.descriptors.size > 0 }
                    .forEach { addOperation(GattSetNotificationOperation(device, controlService.uuid, it.uuid, it.descriptors[0].uuid)) }

            EventBus.getDefault().postSticky(DeviceConnected(this@BleDevice))
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing", "Callback: Error writing GATT Descriptor: " + status)
            }

            operationQueue.remove()
            if (operationQueue.size > 0) {
                operationQueue.element().execute(mGatt)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("LiveRowing", "Characteristic write unsuccessful, status: " + status)
                //disconnectGattServer()
            }

            operationQueue.remove()
            if (operationQueue.size > 0) {
                operationQueue.element().execute(mGatt)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing", "Characteristic read successfully")
            } else {
                Log.e("LiveRowing", "Characteristic read unsuccessful, status: " + status)
                // Trying to read from the Time Characteristic? It doesnt have the property or permissions
                // set to allow this. Normally this would be an error and you would want to:
                // disconnectGattServer()
            }

            (operationQueue.remove() as GattReadCharacteristicOperation).callback(characteristic)
            if (operationQueue.size > 0) {
                operationQueue.element().execute(mGatt)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)

            when (characteristic.uuid.toString()) {
                ROWING_STATUS_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(RowingStatus.fromCharacteristic(characteristic))
                EXTRASTATUS1_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(AdditionalRowingStatus1.fromCharacteristic(characteristic))
                EXTRASTATUS2_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(AdditionalRowingStatus2.fromCharacteristic(characteristic))
                STROKEDATA_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(StrokeData.fromCharacteristic(characteristic))
                EXTRA_STROKEDATA_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(AdditionalStrokeData.fromCharacteristic(characteristic))
                SPLITINTERVAL_DATA_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(SplitIntervalData.fromCharacteristic(characteristic))
                EXTRA_SPLITINTERVAL_DATA_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(AdditionalSplitIntervalData.fromCharacteristic(characteristic))
                ROWING_SUMMARY_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(WorkoutSummary.fromCharacteristic(characteristic))
                EXTRA_ROWING_SUMMARY_CHARACTERISIC_UUID_STRING -> EventBus.getDefault().post(AdditionalWorkoutSummary.fromCharacteristic(characteristic))
                RECEIVE_FROM_PM_CHARACTERISIC_UUID_STRING -> {
                    Log.d("LiveRowing", "RECV < " + characteristic.value.contentToString())
                    if (characteristic.value[characteristic.value.size - 1] == Frame.CSAFE_STOP_FLAG) {
                        if (operationQueue.peek() is GattReadCharacteristicOperation) {
                            (operationQueue.remove() as GattReadCharacteristicOperation).callback(characteristic)
                        }
                        if (operationQueue.size > 0) {
                            operationQueue.element().execute(mGatt)
                        }
                    }

                }
                else -> Log.d("LiveRowing", "Unknown characteristic: " + characteristic.uuid.toString())
            }
        }
    }
}