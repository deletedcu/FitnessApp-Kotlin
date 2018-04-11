package com.liverowing.liverowing.service.operations

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-24.
 */
class GattReadCharacteristicOperation(device: BluetoothDevice, private val serviceUUID: UUID, private val characteristicUUID: UUID, val callback: (characteristic: BluetoothGattCharacteristic) -> Unit?) : GattOperation(device) {
    override fun execute(gatt: BluetoothGatt) {
        Log.d("LiveRowing", "Should read from characteristic: " + characteristicUUID)
        val characteristic = gatt.getService(serviceUUID).getCharacteristic(characteristicUUID)
        gatt.readCharacteristic(characteristic)
    }
}