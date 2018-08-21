package com.liverowing.android.devicescan

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView

interface DeviceScanView : MvpLceView<List<BluetoothDeviceAndStatus>> {
    fun requestBluetoothEnable()
    fun requestLocationPermission()
    fun deviceConnected(name: String)
}