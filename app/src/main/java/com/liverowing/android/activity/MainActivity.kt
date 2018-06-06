package com.liverowing.android.activity

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.liverowing.android.R
import com.liverowing.android.R.id.*
import com.liverowing.android.activity.calculator.CalculatorFragment
import com.liverowing.android.activity.dashboard.DashboardFragment
import com.liverowing.android.activity.settings.SettingsFragment
import com.liverowing.android.service.device.usb.UsbDevice
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.content.IntentFilter
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.widget.Toast
import com.liverowing.android.LiveRowing
import com.liverowing.android.activity.devicescan.DeviceScanActivity
import com.liverowing.android.activity.race.RaceActivity
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.service.messages.*
import com.parse.ParseObject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val ACTION_USB_PERMISSION = "com.liverowing.permissions.USB_PERMISSION"
        private const val SAVE_INSTANCE_FRAGMENT_KEY = "fragmentId"
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        a_main_fab.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                if (menuItem?.itemId == action_just_row) {
                    val justRowWorkout = ParseObject.createWithoutData<WorkoutType>(WorkoutType::class.java, "RxlmI39yME")
                    val workoutSetup = WorkoutSetup(justRowWorkout)
                    EventBus.getDefault().postSticky(workoutSetup)

                    startActivity(Intent(this@MainActivity, RaceActivity::class.java))
                    return true
                }

                val dialog = QuickWorkoutFragment.newInstance()
                dialog.show(supportFragmentManager, QuickWorkoutFragment::class.toString())
                val type = if (menuItem!!.itemId == action_single_distance) 1 else 2
                dialog.mType = type
                return true
            }
        })

        val fragmentManager = supportFragmentManager
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_content, DashboardFragment())
                .commit()

        EventBus.getDefault().register(this)

        if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            val device = intent.getParcelableExtra<android.hardware.usb.UsbDevice>(UsbManager.EXTRA_DEVICE)
            Log.d("LiveRowing", "onCreate: " + device.deviceName)
            EventBus.getDefault().post(DeviceConnectRequest(device))
        } else {
            val device = UsbDevice.findDevice(this@MainActivity)
            if (device != null) {
                checkPermissionsAndConnectUsb(device)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            val device = intent.getParcelableExtra<android.hardware.usb.UsbDevice>(UsbManager.EXTRA_DEVICE)
            Log.d("LiveRowing", "onNewIntent: " + device.deviceName)
            EventBus.getDefault().post(DeviceConnectRequest(device))
        } else if (intent?.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
            Log.d("LiveRowing", "Usb device detached: " + LiveRowing.device)
            EventBus.getDefault().apply {
                removeStickyEvent(DeviceConnectRequest(LiveRowing.device!!))
                post(DeviceDisconnected(LiveRowing.device!!))
            }
        }
    }

    private var deviceConnected = false
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeviceConnected(message: DeviceConnected) {
        deviceConnected = true
        Toast.makeText(this@MainActivity, "Connected to " + message.device.name, Toast.LENGTH_SHORT).show()
        invalidateOptionsMenu()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeviceDisconnected(message: DeviceDisconnected) {
        deviceConnected = false
        Toast.makeText(this@MainActivity, "Disconnected from " + message.device.name, Toast.LENGTH_SHORT).show()
        invalidateOptionsMenu()
    }

    private var snackBar: Snackbar? = null
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNetworkChange(message: NetworkChange) {
        if (!message.isConnected) {
            snackBar = Snackbar.make(a_main_coordinator, "No internet connectivity, LiveRowing will operate with limited functionality.", Snackbar.LENGTH_INDEFINITE)
            snackBar?.setAction("Ok", { snackBar?.dismiss() })
            snackBar?.show()
        } else if (snackBar != null) {
            snackBar?.dismiss()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (deviceConnected) {
            menu!!.findItem(action_scan).setIcon(R.drawable.ic_bluetooth_connected_black)
        } else {
            menu!!.findItem(action_scan).setIcon(R.drawable.ic_bluetooth_black)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scan -> startActivity(Intent(this@MainActivity, DeviceScanActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment: Fragment = when (item.itemId) {
            R.id.nav_dashboard -> DashboardFragment()
            R.id.nav_calculator -> CalculatorFragment()
            nav_settings -> SettingsFragment()
            else -> Fragment()
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit()

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private lateinit var mPermissionIntent: PendingIntent
    private lateinit var mUsbReceiver: BroadcastReceiver
    private fun checkPermissionsAndConnectUsb(device: android.hardware.usb.UsbDevice) {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        if (!manager.hasPermission(device)) {
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
            mUsbReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    Log.d("LiveRowing", action)
                    if (ACTION_USB_PERMISSION == action) {
                        synchronized(this) {
                            val intentDevice = intent.getParcelableExtra<android.hardware.usb.UsbDevice>(UsbManager.EXTRA_DEVICE)
                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                Log.d("LiveRowing", "BroadcastReceiver: Have permission, connect to device")
                                EventBus.getDefault().post(DeviceConnectRequest(intentDevice))
                            } else {
                                Log.d("LiveRowing", "permission denied for device " + device)
                            }
                        }
                    }
                }
            }
            registerReceiver(mUsbReceiver, IntentFilter(ACTION_USB_PERMISSION))
            manager.requestPermission(device, mPermissionIntent)
        } else {
            Log.d("LiveRowing", "checkPermissionsAndConnectUsb: Have permission, connect to device")
            EventBus.getDefault().post(DeviceConnectRequest(device))
        }
    }
}
