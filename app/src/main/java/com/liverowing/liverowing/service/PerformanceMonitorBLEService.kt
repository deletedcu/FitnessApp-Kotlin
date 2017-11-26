package com.liverowing.liverowing.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.liverowing.liverowing.model.pm.*
import java.util.*
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattDescriptor
import android.os.Parcelable
import android.support.v4.content.LocalBroadcastManager
import com.liverowing.liverowing.csafe.Communication
import com.liverowing.liverowing.service.messages.BLEDeviceConnectRequest
import com.liverowing.liverowing.service.messages.BLEDeviceConnected
import com.liverowing.liverowing.service.messages.BLEDeviceDisconnected
import com.liverowing.liverowing.service.messages.ProgramWorkout
import com.liverowing.liverowing.service.operations.GattOperation
import com.liverowing.liverowing.service.operations.GattReadCharacteristicOperation
import com.liverowing.liverowing.service.operations.GattSetNotificationOperation
import com.liverowing.liverowing.service.operations.GattWriteCharacteristicOperation
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class PerformanceMonitorBLEService : Service() {
    companion object {
        const val BROADCAST_CHARACTERISTICS_CHANGED = "characteristicsChanged"
    }

    var mStartMode: Int = 0             // indicates how to behave if the service is killed
    var mBinder: IBinder? = null        // interface for clients that bind
    var mAllowRebind: Boolean = false   // indicates whether onRebind should be used

    lateinit var mDevice: BluetoothDevice
    var Connected: Boolean = false
    var mGatt: BluetoothGatt? = null

    private val operationQueue = LinkedList<GattOperation>()

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        // The service is being created
        mBinder = Binder()
        Log.d("LiveRowing", "Service: onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // The service is starting, due to a call to startService()
        Log.d("LiveRowing", "Service: onStartCommand")
        return mStartMode
    }

    override fun onBind(intent: Intent): IBinder? {
        // A client is binding to the service with bindService()
        Log.d("LiveRowing", "Service: onBind")
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        super.onUnbind(intent)

        // All clients have unbound with unbindService()
        Log.d("LiveRowing", "Service: onUnBind")
        return mAllowRebind
    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d("LiveRowing", "Service: onReBind")
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

        Log.d("LiveRowing", "Service: onDestroy")
        // The service is no longer used and is being destroyed
    }

    @Subscribe
    fun onBLEDeviceConnectRequest(message: BLEDeviceConnectRequest) {
        mDevice = message.device
        if (mDevice.bondState == BluetoothDevice.BOND_BONDED) {
            unpairDevice(mDevice)
        }

        mGatt = mDevice.connectGatt(this@PerformanceMonitorBLEService, false, GattClientCallback(mDevice))
    }

    @Subscribe
    fun onProgramWorkout(message: ProgramWorkout) {
        val workoutType = message.workoutType
        Log.d("LiveRowing", "Should program workout: " + workoutType.name)

        when (workoutType.valueType) {
            1 -> { // Distance
                val distance = workoutType.value!! * 100
                var split = 100
                if (workoutType.splitLength !== null) {
                    split = workoutType.splitLength!!
                } else {
                    split = distance / 5
                    if (split < 100) split = 100
                }

                val workout = Communication().fixedWorkout(
                        WorkoutType.FIXEDDIST_NOSPLITS,
                        distance,
                        split,
                        0
                )

                sendCsafeCommand(workout)
            }
        }
    }

    fun addOperation(operation: GattOperation) {
        operationQueue.add(operation)
        if (operationQueue.size == 1) {
            operation.execute(mGatt!!)
        }
    }

    fun sendCsafeCommand(csafe: ByteArray) {
        Log.d("LiveRowing", csafe.contentToString())

        var i = 0
        while (i <= csafe.size - 1) {
            val bytes = csafe.slice(IntRange(i, Math.min(i+19, csafe.size - 1))).toByteArray()
            i += bytes.size

            addOperation(
                    GattWriteCharacteristicOperation(
                            mDevice,
                            UUID.fromString("ce060020-43e5-11e4-916c-0800200c9a66"),
                            UUID.fromString("ce060021-43e5-11e4-916c-0800200c9a66"),
                            bytes
                    )
            )
        }
    }

    private fun unpairDevice(device: BluetoothDevice) {
        val btClass = Class.forName("android.bluetooth.BluetoothDevice")
        val removeBondMethod = btClass.getMethod("removeBond")
        removeBondMethod.invoke(device)
    }

    inner class Binder : android.os.Binder() {
        val service: PerformanceMonitorBLEService
            get() = this@PerformanceMonitorBLEService
    }

    private inner class GattClientCallback(mDevice: BluetoothDevice) : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.d("LiveRowing", "onConnectionStateChange newState: " + newState)

            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.e("LiveRowing", "Connection Gatt failure status " + status)
                Connected = false
                //disconnectGattServer()
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                // handle anything not SUCCESS as failure
                Log.e("LiveRowing", "Connection not GATT success status " + status)
                Connected = false
                //disconnectGattServer()
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("LiveRowing", "Connected to device " + gatt.device.address)
                Connected = true
                mGatt = gatt
                gatt.discoverServices()

                EventBus.getDefault().postSticky(BLEDeviceConnected(mDevice))
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("LiveRowing", "Disconnected from device")

                Connected = false
                mGatt = null

                EventBus.getDefault().apply {
                    removeStickyEvent(BLEDeviceConnected(mDevice))
                    post(BLEDeviceDisconnected(mDevice))
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing", "Device service discovery unsuccessful, status " + status)
                return
            }

            Log.d("LiveRowing", "Initializing: setting write type and enabling notification for rowing service")
            val rowingService = gatt.getService(UUID.fromString("ce060030-43e5-11e4-916c-0800200c9a66"))
            rowingService.characteristics
                    .filter { it.descriptors.size > 0 }
                    .forEach { addOperation(GattSetNotificationOperation(mDevice, rowingService.uuid, it.uuid, it.descriptors[0].uuid)) }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing", "Callback: Wrote GATT Descriptor successfully.")
            } else {
                Log.d("LiveRowing", "Callback: Error writing GATT Descriptor: " + status)
            }

            operationQueue.remove()
            if (operationQueue.size > 0) {
                operationQueue.element().execute(mGatt!!)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing", "Characteristic written successfully")
            } else {
                Log.e("LiveRowing", "Characteristic write unsuccessful, status: " + status)
                //disconnectGattServer()
            }

            operationQueue.remove()
            if (operationQueue.size > 0) {
                operationQueue.element().execute(mGatt!!)
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
                // disconnectGattServer();
            }

            operationQueue.remove()
            if (operationQueue.size > 0) {
                operationQueue.element().execute(mGatt!!)
            }
        }


        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)

            when (characteristic.uuid.toString()) {
                "ce060031-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(RowingStatus.fromCharacteristic(characteristic))
                "ce060032-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(AdditionalRowingStatus1.fromCharacteristic(characteristic))
                "ce060033-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(AdditionalRowingStatus2.fromCharacteristic(characteristic))
                "ce060035-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(StrokeData.fromCharacteristic(characteristic))
                "ce060036-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(AdditionalStrokeData.fromCharacteristic(characteristic))
                "ce060037-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(SplitIntervalData.fromCharacteristic(characteristic))
                "ce060038-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(AdditionalSplitIntervalData.fromCharacteristic(characteristic))
                "ce060039-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(WorkoutSummary.fromCharacteristic(characteristic))
                "ce06003a-43e5-11e4-916c-0800200c9a66" -> EventBus.getDefault().post(AdditionalWorkoutSummary.fromCharacteristic(characteristic))
            }
        }
    }
}
