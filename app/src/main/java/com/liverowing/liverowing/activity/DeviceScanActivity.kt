package com.liverowing.liverowing.activity

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.liverowing.liverowing.R
import android.bluetooth.BluetoothAdapter
import android.app.Activity
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Build
import android.util.Log
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.os.Handler
import android.os.ParcelUuid
import java.util.*
import android.view.InputDevice.getDevice
import android.bluetooth.le.ScanCallback




fun Context.DeviceScanIntent(): Intent {
    return Intent(this, DeviceScanActivity::class.java).apply {}
}

class DeviceScanActivity : AppCompatActivity() {
    private var mScanning: Boolean = false
    private var mHandler: Handler? = null
    private var mScanResults: Map<String, BluetoothDevice>? = null

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private var mScanCallback: ScanCallback? = null

    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val REQUEST_FINE_LOCATION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_scan)

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth LE not supported.", Toast.LENGTH_SHORT).show()
            finish()
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        if (mBluetoothAdapter === null) {
            Toast.makeText(this, "Bluetooth is not supported.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        startScan()
    }

    private fun startScan() {
        if (!hasPermissions() || mScanning) {
            return
        }

        mScanResults = HashMap()
        mScanCallback = BtleScanCallback(mScanResults as HashMap<String, BluetoothDevice>)

        mBluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner

        // Note: Filtering does not work the same (or at all) on most devices. It also is unable to
        // search for a mask or anything less than a full UUID.
        // Unless the full UUID of the server is known, manual filtering may be necessary.
        // For example, when looking for a brand of device that contains a char sequence in the UUID
        val scanFilter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString("CE060000-43E5-11E4-916C-0800200C9A66"))
                .build()
        val filters = ArrayList<ScanFilter>()
        filters.add(scanFilter)

        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()

        mBluetoothLeScanner!!.startScan(filters, settings, mScanCallback)

        mHandler = Handler()
        mHandler!!.postDelayed({ this.stopScan() }, 5000)
        mScanning = true

        Log.d("LiveRowing", "Started scan")
    }

    private fun stopScan() {
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled && mBluetoothLeScanner != null) {
            mBluetoothLeScanner!!.stopScan(mScanCallback)
            Log.d("LiveRowing", "Stopped scan")
            //scanComplete()
        }

        mScanCallback = null
        mScanning = false
        mHandler = null
    }

    private fun hasPermissions(): Boolean {
        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {
            requestBluetoothEnable()
            return false
        } else if (!hasLocationPermissions()) {
            requestLocationPermission()
            return false
        }
        return true
    }

    private fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        Log.d("LiveRowing", "Requested user enables Bluetooth. Try starting the scan again.")
    }

    private fun hasLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
        }
    }

    private class BtleScanCallback internal constructor(private val mScanResults: MutableMap<String, BluetoothDevice>) : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            addScanResult(result)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            for (result in results) {
                addScanResult(result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.d("LiveRowing", errorCode.toString())
        }

        private fun addScanResult(result: ScanResult) {
            val device = result.device
            val deviceAddress = device.address
            Log.d("LiveRowing", deviceAddress)
            mScanResults.put(deviceAddress, device)
        }
    }
}