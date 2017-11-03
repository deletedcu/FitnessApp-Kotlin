package com.liverowing.liverowing.service

import android.app.Service
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.liverowing.liverowing.model.pm.*
import java.util.*
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothGattCallback



class PerformanceMonitorBLEService : Service() {
    var mStartMode: Int = 0             // indicates how to behave if the service is killed
    var mBinder: IBinder? = null        // interface for clients that bind
    var mAllowRebind: Boolean = false   // indicates whether onRebind should be used

    lateinit var mDevice: BluetoothDevice
    var mGatt: BluetoothGatt? = null

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
        device.connectGatt(this, false, GattClientCallback())
    }

    inner class Binder : android.os.Binder() {
        val service: PerformanceMonitorBLEService
            get() = this@PerformanceMonitorBLEService
    }

    private inner class GattClientCallback : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.d("LiveRowing","onConnectionStateChange newState: " + newState)

            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.e("LiveRowing","Connection Gatt failure status " + status)
                //disconnectGattServer()
                return
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                // handle anything not SUCCESS as failure
                Log.e("LiveRowing","Connection not GATT success status " + status)
                //disconnectGattServer()
                return
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("LiveRowing","Connected to device " + gatt.device.address)
                //setConnected(true)
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("LiveRowing","Disconnected from device")
                //disconnectGattServer()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing","Device service discovery unsuccessful, status " + status)
                return
            }

            val rowingService = gatt.getService(UUID.fromString("ce060030-43e5-11e4-916c-0800200c9a66"))

            Log.d("LiveRowing","Initializing: setting write type and enabling notification")
            for (characteristic in rowingService.characteristics) {
                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                enableCharacteristicNotification(gatt, characteristic)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing","Characteristic written successfully")
            } else {
                Log.e("LiveRowing","Characteristic write unsuccessful, status: " + status)
                //disconnectGattServer()
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("LiveRowing","Characteristic read successfully")
                readCharacteristic(characteristic)
            } else {
                Log.e("LiveRowing","Characteristic read unsuccessful, status: " + status)
                // Trying to read from the Time Characteristic? It doesnt have the property or permissions
                // set to allow this. Normally this would be an error and you would want to:
                // disconnectGattServer();
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.d("LiveRowing","Characteristic changed, " + characteristic.uuid.toString())
            readCharacteristic(characteristic)
        }

        private fun enableCharacteristicNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val characteristicWriteSuccess = gatt.setCharacteristicNotification(characteristic, true)
            if (characteristicWriteSuccess) {
                Log.d("LiveRowing","Characteristic notification set successfully for " + characteristic.uuid.toString())
                if (characteristic.descriptors.size > 0) {
                    Log.d("LiveRowing", "Descriptor written.")
                    val descriptor = characteristic.descriptors[0]
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
            } else {
                Log.e("LiveRowing","Characteristic notification set failure for " + characteristic.uuid.toString())
            }
        }

        private fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
            val messageBytes = characteristic.value
            Log.d("LiveRowing","Read: " + messageBytes.())
        }
    }
}
