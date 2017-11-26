package com.liverowing.liverowing.service.operations

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-24.
 */
class GattSetNotificationOperation(device: BluetoothDevice, private val serviceUUID: UUID, private val characteristicUUID: UUID, private val descriptorUUID: UUID) : GattOperation(device) {
    override fun execute(gatt: BluetoothGatt) {
        val characteristic = gatt.getService(serviceUUID).getCharacteristic(characteristicUUID)
        characteristic.writeType = WRITE_TYPE_DEFAULT
        val characteristicWriteSuccess = gatt.setCharacteristicNotification(characteristic, true)
        if (characteristicWriteSuccess) {
            Log.d("LiveRowing", "Characteristic notification set successfully for " + characteristicUUID)
            val descriptor = characteristic.getDescriptor(descriptorUUID)
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        } else {
            Log.e("LiveRowing", "Characteristic notification set failure for " + characteristicUUID)
        }
    }
}