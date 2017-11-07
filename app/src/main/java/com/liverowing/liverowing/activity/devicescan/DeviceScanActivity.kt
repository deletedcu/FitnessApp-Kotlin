package com.liverowing.liverowing.activity.devicescan

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.liverowing.liverowing.R
import com.liverowing.liverowing.extensions.action
import com.liverowing.liverowing.extensions.requestPermission
import com.liverowing.liverowing.extensions.shouldShowPermissionRationale
import com.liverowing.liverowing.extensions.snack
import kotlinx.android.synthetic.main.activity_device_scan.*
import java.util.*
import android.support.v7.widget.LinearLayoutManager
import com.liverowing.liverowing.adapter.BLEDeviceAdapter
import com.liverowing.liverowing.service.PerformanceMonitorBLEService


fun Context.DeviceScanIntent(): Intent {
    return Intent(this, DeviceScanActivity::class.java).apply {}
}

class DeviceScanActivity : AppCompatActivity() {
    private var mScanning: Boolean = false
    private var mHandler: Handler? = null
    private var mDevices = mutableListOf<BluetoothDevice>()

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private var mScanCallback: ScanCallback? = null

    private var mServiceBound = false
    private lateinit var mPerformanceMonitorBLEService: PerformanceMonitorBLEService
    private lateinit var mServiceConnection: ServiceConnection

    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val REQUEST_FINE_LOCATION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_scan)

        setSupportActionBar(a_device_scan_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth LE is not supported.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        if (mBluetoothAdapter === null) {
            Toast.makeText(this, "Bluetooth is not supported.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        a_device_scan_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@DeviceScanActivity, LinearLayoutManager.VERTICAL, false)
            adapter = BLEDeviceAdapter(mDevices, { device ->
                run {
                    mPerformanceMonitorBLEService.connectToDevice(device)
                }
            })
        }

        startScan()
    }


    override fun onStart() {
        super.onStart()

        mServiceConnection = object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                mServiceBound = true
                mPerformanceMonitorBLEService = (binder as PerformanceMonitorBLEService.Binder).service
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mServiceBound = false
            }
        }

        val intent = Intent(this, PerformanceMonitorBLEService::class.java)
        startService(intent)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()

        if (mServiceBound) {
            unbindService(mServiceConnection)
            mServiceBound = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) startScan() else {
                Toast.makeText(this, "Enable Bluetooth to enable scanning for devices.", Toast.LENGTH_LONG).show()
                supportFinishAfterTransition()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults[0] == 0) startScan()  else {
                Toast.makeText(this, "Access to location is needed to be able to scan for devices.", Toast.LENGTH_LONG).show()
                supportFinishAfterTransition()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun startScan() {
        if (!hasPermissions() || mScanning) {
            return
        }

        mDevices.clear()
        mBluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner

        val scanFilter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString("CE060000-43E5-11E4-916C-0800200C9A66"))
                .build()
        val filters = ArrayList<ScanFilter>()
        filters.add(scanFilter)

        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

        mScanCallback = (object: ScanCallback() {
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
                if (!mDevices.contains(result.device)) {
                    result.device.createBond()
                    mDevices.add(result.device)
                    a_device_scan_recyclerview.adapter.notifyDataSetChanged()
                }
            }
        })
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
        Log.d("LiveRowing", "Requested that the  user enables Bluetooth.")
    }

    private fun hasLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    private fun requestLocationPermission() {
        if (shouldShowPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            a_device_scan_root.snack("LiveRowing needs access to location to be able to scan for devices.", Snackbar.LENGTH_INDEFINITE, {
                action("Action") { requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION) }
            })
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION)
        }
    }
}