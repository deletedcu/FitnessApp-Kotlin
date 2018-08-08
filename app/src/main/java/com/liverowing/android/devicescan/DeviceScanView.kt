package com.liverowing.android.devicescan

import android.bluetooth.BluetoothDevice
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView

interface DeviceScanView : MvpLceView<List<BluetoothDeviceAndStatus>> {
    fun requestBluetoothEnable()
    fun requestLocationPermission()
}