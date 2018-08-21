package com.liverowing.android.devicescan

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.ble.PM5Manager
import com.liverowing.android.ble.PM5ManagerCallbacks
import com.liverowing.android.devicescan.BluetoothDeviceAndStatus.Companion.Status.Disconnected
import com.liverowing.android.model.messages.DeviceDisconnectRequest
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class DeviceScanPresenter(private val ctx: Context) : MvpBasePresenter<DeviceScanView>(), PM5ManagerCallbacks {
    override fun onDeviceDisconnecting(device: BluetoothDevice?) {
        Timber.d("** onDeviceDisconnecting")
    }

    override fun onDeviceDisconnected(device: BluetoothDevice?) {
        Timber.d("** onDeviceDisconnected")
    }

    override fun onDeviceConnected(device: BluetoothDevice?) {
        Timber.d("** onDeviceConnected")
    }

    override fun onDeviceNotSupported(device: BluetoothDevice?) {
        Timber.d("** onDeviceNotSupported")
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

    override fun onDeviceReady(device: BluetoothDevice?) {
        Timber.d("** onDeviceReady")
    }

    override fun onError(device: BluetoothDevice?, message: String?, errorCode: Int) {
        Timber.d("** onError")
    }

    override fun onDeviceConnecting(device: BluetoothDevice?) {
        Timber.d("** onDeviceConnecting")
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var scanCallback: ScanCallback? = null
    private var scanning: Boolean = false
    private val devices = mutableMapOf<String, BluetoothDeviceAndStatus>()

    // TODO: This should probably utilize the Repository pattern instead but _meh_ for now.
    fun loadDevices(pullToRefresh: Boolean) {
        ifViewAttached { it.showLoading(pullToRefresh) }

        if (scanning) { stopScanning() }
        startScanning()
    }

    private fun startScanning() {
        if (!ctx.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ifViewAttached { it.showError(Exception("Bluetooth LE is not supported on this device."), false) }
            return
        }

        val bluetoothManager = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter === null) {
            ifViewAttached { it.showError(Exception("Bluetooth is not supported on this device."), false) }
            return
        }

        if (!checkPermissions()) {
            return
        }

        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

        scanCallback = (object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                addScanResult(result)
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                for (result in results) {
                    addScanResult(result)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Timber.e("onScanFailed: $errorCode")
            }

            private fun addScanResult(result: ScanResult) {
                if (!devices.containsKey(result.device.address)) {
                    devices[result.device.address] = BluetoothDeviceAndStatus(result.device, Disconnected)
                    ifViewAttached {
                        it.setData(devices.values.toList())
                        it.showContent()
                    }
                }
            }
        })

        val scanFilter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString("CE060000-43E5-11E4-916C-0800200C9A66"))
                .build()

        val filters = listOf(scanFilter)
        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()

        scanning = true
        bluetoothLeScanner?.startScan(filters, settings, scanCallback)
    }

    fun stopScanning() {
        if (bluetoothAdapter !== null && bluetoothAdapter!!.isEnabled && bluetoothLeScanner !== null) {
            bluetoothLeScanner?.stopScan(scanCallback)
            Timber.d("Stopped scanning for devices")
        }

        scanCallback = null
        scanning = false
    }

    private fun checkPermissions(): Boolean {
        if (bluetoothAdapter === null || !bluetoothAdapter!!.isEnabled) {
            ifViewAttached {
                it.showError(Exception("Enable Bluetooth to scan for devices."), false)
                it.requestBluetoothEnable()
            }
            return false
        } else if (!hasLocationPermissions()) {
            ifViewAttached {
                it.showError(Exception("Grant permission to location to scan for devices."), false)
                it.requestLocationPermission()
            }
            return false
        }

        return true
    }

    private fun hasLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    private var pm5Manager: PM5Manager? = null
    fun connectToDevice(device: BluetoothDevice) {
        pm5Manager = PM5Manager(ctx)
        pm5Manager?.setGattCallbacks(this)

        Timber.d("** Connecting to ${device.address}")
        pm5Manager?.connect(device)?.done {
            Timber.d("** done")
        }?.fail { device, status ->
            Timber.d("** $device, $status")
        }?.enqueue()
        /*
        EventBus.getDefault().post(DeviceConnectRequest(device))
        if (devices.contains(device.address)) {
            devices[device.address]?.status = Connecting
            ifViewAttached {
                it.setData(devices.values.toList())
            }
        }
        */
    }

    fun disconnectFromDevice(device: BluetoothDevice) {
        EventBus.getDefault().post(DeviceDisconnectRequest(device))
        if (devices.contains(device.address)) {
            devices[device.address]?.status = Disconnected
            ifViewAttached {
                it.setData(devices.values.toList())
            }
        }
    }
}