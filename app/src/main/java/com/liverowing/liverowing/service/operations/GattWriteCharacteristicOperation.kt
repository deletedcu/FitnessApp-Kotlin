package com.liverowing.liverowing.service.operations

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-24.
 */
class GattWriteCharacteristicOperation(device: BluetoothDevice, private val serviceUUID: UUID, private val characteristicUUID: UUID, private val value: ByteArray) : GattOperation(device) {
    override fun execute(gatt: BluetoothGatt) {
        val builder = StringBuilder()
        for (b in value) {
            builder.append(String.format("%02x", b))
        }

        Log.d("LiveRowing", "Should write: " + builder.toString())
        val characteristic = gatt.getService(serviceUUID).getCharacteristic(characteristicUUID)
        characteristic.value = value
        gatt.writeCharacteristic(characteristic)
    }
}