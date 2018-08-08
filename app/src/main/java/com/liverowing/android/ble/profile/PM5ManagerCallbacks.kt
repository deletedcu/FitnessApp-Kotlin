package com.liverowing.android.ble.profile

import no.nordicsemi.android.ble.BleManagerCallbacks

interface PM5ManagerCallbacks : BleManagerCallbacks {
    /**
     * Called when a button was pressed or released on device
     *
     * @param state true if the button was pressed, false if released
     */
    fun onDataReceived(state: Boolean)

    /**
     * Called when the data has been sent to the connected device.
     *
     * @param state true when LED was enabled, false when disabled
     */
    fun onDataSent(state: Boolean)
}