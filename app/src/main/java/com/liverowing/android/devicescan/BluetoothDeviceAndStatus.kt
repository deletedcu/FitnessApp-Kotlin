package com.liverowing.android.devicescan

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceAndStatus(val device: BluetoothDevice, var status: Status)  {
    companion object {
        enum class Status {
            Disconnected,
            Connecting,
            Connected
        }
    }
}
