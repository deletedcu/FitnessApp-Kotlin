package com.liverowing.liverowing.activity.devicescan

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
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
import android.view.Menu
import android.view.View
import com.liverowing.liverowing.R.id.action_scan
import com.liverowing.liverowing.adapter.BLEDeviceAdapter
import com.liverowing.liverowing.service.messages.DeviceConnectRequest
import com.liverowing.liverowing.service.messages.DeviceConnected
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


fun Context.DeviceScanIntent(): Intent {
    return Intent(this, DeviceScanActivity::class.java).apply {}
}

class DeviceScanActivity : AppCompatActivity() {
    private lateinit var  mHandler: Handler

    private var mScanning: Boolean = false
    private var mScanResult = mutableListOf<BLEDeviceAdapter.BLEDevice>()

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
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@DeviceScanActivity, LinearLayoutManager.VERTICAL, false).apply {
                isAutoMeasureEnabled = true
            }
            adapter = BLEDeviceAdapter(mScanResult, { result ->
                run {
                    EventBus.getDefault().post(DeviceConnectRequest(result.device))
                }
            })
        }

        startScan()
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeviceConnected(message: DeviceConnected) {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_device_scan, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(action_scan)?.isEnabled = !mScanning
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }

            action_scan -> {
                startScan()
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

        a_device_scan_progressbar.visibility = View.VISIBLE
        invalidateOptionsMenu()

        mScanResult.clear()
        a_device_scan_recyclerview.adapter.notifyDataSetChanged()
        mBluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner

        val scanFilter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString("CE060000-43E5-11E4-916C-0800200C9A66"))
                .build()
        val filters = ArrayList<ScanFilter>()
        filters.add(scanFilter)

        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()

        mScanCallback = (object: ScanCallback() {
            private val mDevices = mutableMapOf<String, BLEDeviceAdapter.BLEDevice>()
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
                runOnUiThread {
                    if (!mDevices.containsKey(result.device.address)) {
                        mDevices[result.device.address] = BLEDeviceAdapter.BLEDevice(result.device, result.rssi)
                        mScanResult.add(mDevices[result.device.address]!!)
                    } else {
                        mDevices[result.device.address]!!.rssi = result.rssi
                    }

                    a_device_scan_recyclerview.adapter.notifyDataSetChanged()
                }
            }
        })
        mBluetoothLeScanner!!.startScan(filters, settings, mScanCallback)

        mHandler = Handler()
        mHandler.postDelayed({ this.stopScan() }, 5000)
        mScanning = true

        Log.d("LiveRowing", "Started scan")
    }

    private fun stopScan() {
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled && mBluetoothLeScanner != null) {
            mBluetoothLeScanner!!.stopScan(mScanCallback)
            Log.d("LiveRowing", "Stopped scan")
        }

        a_device_scan_progressbar.visibility = View.GONE
        invalidateOptionsMenu()

        mScanCallback = null
        mScanning = false
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
        Log.d("LiveRowing", "Requested that the user enables Bluetooth.")
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