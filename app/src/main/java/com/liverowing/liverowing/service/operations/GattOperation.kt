package com.liverowing.liverowing.service.operations

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

/**
 * Created by henrikmalmberg on 2017-11-24.
 */
abstract class GattOperation(private var device: BluetoothDevice) {
    abstract fun execute(gatt: BluetoothGatt)
}