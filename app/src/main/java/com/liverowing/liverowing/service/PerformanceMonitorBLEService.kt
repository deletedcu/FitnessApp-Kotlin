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

    private val descriptorWriteQueue = LinkedList<BluetoothGattDescriptor>()
    private val characteristicWriteQueue = LinkedList<BluetoothGattCharacteristic>()
    private val characteristicReadQueue = LinkedList<BluetoothGattCharacteristic>()

    private lateinit var controlTx: BluetoothGattCharacteristic
    private lateinit var controlRx: BluetoothGattCharacteristic

    override fun onCreate() {
        // The service is being created
        mBinder = Binder()
        Log.d("LiveRowing", "Service: onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
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
        // All clients have unbound with unbindService()
        Log.d("LiveRowing", "Service: onUnBind")
        return mAllowRebind
    }

    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d("LiveRowing", "Service: onReBind")
    }

    override fun onDestroy() {
        Log.d("LiveRowing", "Service: onDestroy")
        // The service is no longer used and is being destroyed
    }

    fun connectToDevice(device: BluetoothDevice) {
        mDevice = device
        if (device.bondState == BluetoothDevice.BOND_BONDED) {
            unpairDevice(mDevice)
        }

        mGatt = mDevice.connectGatt(this@PerformanceMonitorBLEService, true, GattClientCallback(mDevice))
    }

    fun sendCsafeCommand(csafe: ByteArray) {
        controlTx.value = csafe
        writeGattCharacteristic(controlTx)
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        Log.d("LiveRowing", "Should read characteristic")
        characteristicReadQueue.add(characteristic)
        if (characteristicReadQueue.size == 1 && descriptorWriteQueue.size == 0 && characteristicWriteQueue.size == 0) {
            mGatt!!.readCharacteristic(characteristic)
        }
    }

    fun writeGattCharacteristic(characteristic: BluetoothGattCharacteristic) {
        Log.d("LiveRowing", "Should write characteristic")
        characteristicWriteQueue.add(characteristic)
        if (characteristicWriteQueue.size == 1 && descriptorWriteQueue.size == 0) {
            mGatt!!.writeCharacteristic(characteristic)
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
                return
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                // handle anything not SUCCESS as failure
                Log.e("LiveRowing", "Connection not GATT success status " + status)
                Connected = false
                //disconnectGattServer()
                return
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("LiveRowing", "Connected to device " + gatt.device.address)
                Connected = true
                mGatt = gatt
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("LiveRowing", "Disconnected from device")

                Connected = false
                mGatt = null
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
            for (characteristic in rowingService.characteristics) {
                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                enableCharacteristicNotification(gatt, characteristic)
            }

            val controlService = gatt.getService(UUID.fromString("ce060020-43e5-11e4-916c-0800200c9a66"))
            controlTx = controlService.getCharacteristic(UUID.fromString("ce060021-43e5-11e4-916c-0800200c9a66"))
            controlRx = controlService.getCharacteristic(UUID.fromString("ce060022-43e5-11e4-916c-0800200c9a66"))
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing", "Callback: Wrote GATT Descriptor successfully.");
            } else {
                Log.d("LiveRowing", "Callback: Error writing GATT Descriptor: " + status);
            }

            descriptorWriteQueue.remove()
            when {
                descriptorWriteQueue.size > 0 -> mGatt!!.writeDescriptor(descriptorWriteQueue.element())
                characteristicWriteQueue.size > 0 -> mGatt!!.writeCharacteristic(characteristicWriteQueue.element())
                characteristicReadQueue.size > 0 -> mGatt!!.readCharacteristic(characteristicReadQueue.element())
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

            characteristicWriteQueue.remove()
            when {
                descriptorWriteQueue.size > 0 -> mGatt!!.writeDescriptor(descriptorWriteQueue.element())
                characteristicWriteQueue.size > 0 -> mGatt!!.writeCharacteristic(characteristicWriteQueue.element())
                characteristicReadQueue.size > 0 -> mGatt!!.readCharacteristic(characteristicReadQueue.element())
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing", "Characteristic read successfully")
                Log.d("LiveRowing", characteristic.value.toString())
            } else {
                Log.e("LiveRowing", "Characteristic read unsuccessful, status: " + status)
                // Trying to read from the Time Characteristic? It doesnt have the property or permissions
                // set to allow this. Normally this would be an error and you would want to:
                // disconnectGattServer();
            }

            characteristicReadQueue.remove()
            when {
                descriptorWriteQueue.size > 0 -> mGatt!!.writeDescriptor(descriptorWriteQueue.element())
                characteristicWriteQueue.size > 0 -> mGatt!!.writeCharacteristic(characteristicWriteQueue.element())
                characteristicReadQueue.size > 0 -> mGatt!!.readCharacteristic(characteristicReadQueue.element())
            }
        }


        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)

            when (characteristic.uuid.toString()) {
                "ce060031-43e5-11e4-916c-0800200c9a66" -> broadcast(RowingStatus.fromCharacteristic(characteristic))
                "ce060032-43e5-11e4-916c-0800200c9a66" -> broadcast(AdditionalRowingStatus1.fromCharacteristic(characteristic))
                "ce060033-43e5-11e4-916c-0800200c9a66" -> broadcast(AdditionalRowingStatus2.fromCharacteristic(characteristic))
                "ce060035-43e5-11e4-916c-0800200c9a66" -> broadcast(StrokeData.fromCharacteristic(characteristic))
                "ce060036-43e5-11e4-916c-0800200c9a66" -> broadcast(AdditionalStrokeData.fromCharacteristic(characteristic))
                "ce060037-43e5-11e4-916c-0800200c9a66" -> broadcast(SplitIntervalData.fromCharacteristic(characteristic))
                "ce060038-43e5-11e4-916c-0800200c9a66" -> broadcast(AdditionalSplitIntervalData.fromCharacteristic(characteristic))
                "ce060039-43e5-11e4-916c-0800200c9a66" -> broadcast(WorkoutSummary.fromCharacteristic(characteristic))
                "ce06003a-43e5-11e4-916c-0800200c9a66" -> broadcast(AdditionalWorkoutSummary.fromCharacteristic(characteristic))
                else -> Log.d("LiveRowing", "Unknown UUID: " + characteristic.uuid.toString())
            }
        }

        fun writeGattDescriptor(descriptor: BluetoothGattDescriptor) {
            descriptorWriteQueue.add(descriptor)
            if (descriptorWriteQueue.size == 1) {
                mGatt!!.writeDescriptor(descriptor)
            }
        }

        private fun broadcast(data: Parcelable) {
            LocalBroadcastManager.getInstance(this@PerformanceMonitorBLEService)
                    .sendBroadcast(Intent(BROADCAST_CHARACTERISTICS_CHANGED).apply {
                        putExtra("characteristic", data::class.java.simpleName)
                        putExtra("data", data)
                    })
        }

        private fun enableCharacteristicNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val characteristicWriteSuccess = gatt.setCharacteristicNotification(characteristic, true)
            if (characteristicWriteSuccess) {
                Log.d("LiveRowing", "Characteristic notification set successfully for " + characteristic.uuid.toString())
                if (characteristic.descriptors.size > 0) {
                    val descriptor = characteristic.descriptors[0]
                    descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    writeGattDescriptor(descriptor)
                }
            } else {
                Log.e("LiveRowing", "Characteristic notification set failure for " + characteristic.uuid.toString())
            }
        }
    }
}
