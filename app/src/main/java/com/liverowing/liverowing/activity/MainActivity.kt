package com.liverowing.liverowing.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.liverowing.liverowing.LiveRowing.Companion.BluetoothDeviceConnected
import com.liverowing.liverowing.R
import com.liverowing.liverowing.R.id.*
import com.liverowing.liverowing.activity.dashboard.DashboardFragment
import com.liverowing.liverowing.activity.devicescan.DeviceScanIntent
import com.liverowing.liverowing.activity.race.RaceActivity
import com.liverowing.liverowing.service.messages.BLEDeviceConnected
import com.liverowing.liverowing.service.messages.BLEDeviceDisconnected
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


fun Context.MainIntent(): Intent {
    return Intent(this, MainActivity::class.java).apply {}
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mBluetoothConnected = false

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        a_main_fab.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                if (menuItem?.itemId == action_just_row) {
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
                .replace(R.id.main_content, DashboardFragment.newInstance())
                .commit()

        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBLEDeviceConnected(message: BLEDeviceConnected) {
        invalidateOptionsMenu()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBLEDeviceDisconnected(message: BLEDeviceDisconnected) {
        invalidateOptionsMenu()
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
        if (BluetoothDeviceConnected) {
            menu!!.findItem(action_scan).setIcon(R.drawable.ic_bluetooth_connected_black)
        } else {
            menu!!.findItem(action_scan).setIcon(R.drawable.ic_bluetooth_black)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_scan -> { startActivity(DeviceScanIntent()); return true }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_dashboard -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
