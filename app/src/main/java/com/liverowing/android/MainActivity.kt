package com.liverowing.android

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        a_main_nav_view.setNavigationItemSelectedListener(this@MainActivity)
        drawerLayout.addDrawerListener(this@MainActivity)

    }

    override fun onResume() {
        super.onResume()

        setSupportActionBar(a_main_toolbar)
        navController = Navigation.findNavController(this, R.id.a_main_content)
        NavigationUI.setupWithNavController(a_main_toolbar, navController, drawerLayout)
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDrawerStateChanged(newState: Int) {

    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerClosed(drawerView: View) {
        val item = a_main_nav_view.checkedItem
        item?.isChecked = false

        when (item?.itemId) {
            R.id.nav_calculators -> navController.navigate(R.id.calculatorsFragment)
            R.id.nav_workout_history-> navController.navigate(R.id.workoutHistoryFragment)
            R.id.nav_workout_browser -> navController.navigate(R.id.workoutBrowserFragment)
            R.id.nav_bluetooth_devices -> navController.navigate(R.id.deviceScanFragment)
        }
    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        return true
    }
}
