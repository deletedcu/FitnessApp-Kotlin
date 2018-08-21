package com.liverowing.android.pm.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.liverowing.android.model.messages.*
import com.liverowing.android.pm.PMDevice
import no.nordicsemi.android.ble.BleManagerCallbacks
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class PM5BleDevice(private val ctx: Context, private val device: BluetoothDevice) : PMDevice, BleManagerCallbacks {
    private val eventBus = EventBus.getDefault()
    private val manager = PM5Manager(ctx)

    override fun connect() {
        if (manager.isConnected) {
            manager.disconnect().enqueue()
        }

        manager.setGattCallbacks(this@PM5BleDevice)
        manager.connect(device).enqueue()
    }

    override fun disconnect() {
        manager.disconnect()
    }

    override fun onDeviceConnecting(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.post(DeviceConnecting(device))
        }
    }

    override fun onDeviceConnected(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.removeStickyEvent(DeviceDisconnected::class.java)
            eventBus.postSticky(DeviceConnected(device))
        }
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.post(DeviceDisconnecting(device))
        }
    }

    override fun onDeviceDisconnected(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.removeStickyEvent(DeviceConnected::class.java)
            eventBus.postSticky(DeviceDisconnected(device))
        }
    }

    override fun onDeviceReady(device: BluetoothDevice?) {
        if (device != null) {
            eventBus.post(DeviceReady(device, device.name))
        }
    }

    override fun onDeviceNotSupported(device: BluetoothDevice?) {
        Timber.d("** onDeviceNotSupported")
    }

    override fun onError(device: BluetoothDevice?, message: String?, errorCode: Int) {
        Timber.d("** onError")
    }

    override fun onBondingFailed(device: BluetoothDevice?) {
        Timber.d("** onBondingFailed")
    }

    override fun onServicesDiscovered(device: BluetoothDevice?, optionalServicesFound: Boolean) {
        Timber.d("** onServicesDiscovered")
    }

    override fun onBondingRequired(device: BluetoothDevice?) {
        Timber.d("** onBondingRequired")
    }

    override fun onLinkLossOccurred(device: BluetoothDevice?) {
        Timber.d("** onLinkLossOccurred")
    }

    override fun onBonded(device: BluetoothDevice?) {
        Timber.d("** onBonded")
    }
}