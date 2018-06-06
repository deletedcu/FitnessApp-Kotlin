package com.liverowing.android.service.operations

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
        Log.d("LiveRowing", "SEND > " + value.contentToString())
        val characteristic = gatt.getService(serviceUUID).getCharacteristic(characteristicUUID)
        characteristic.value = value
        gatt.writeCharacteristic(characteristic)
    }
}