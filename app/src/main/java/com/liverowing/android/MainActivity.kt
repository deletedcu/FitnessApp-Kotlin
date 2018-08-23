package com.liverowing.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var mImmersiveState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService(Intent(this@MainActivity, PMService::class.java))
        navController = Navigation.findNavController(this, R.id.a_main_nav_host)
    }

    override fun onBackPressed() {
        if (a_main_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            a_main_drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            if (mImmersiveState) {
                hideSystemUi()
            } else {
                showSystemUi()
            }
        }
    }

    fun setImmersiveModeState(state: Boolean) {
        mImmersiveState = state
        onWindowFocusChanged(true)
    }

    private fun showSystemUi() {
        Timber.d("** showSystemUi")
        a_main_drawer_layout.fitsSystemWindows = true
        a_main_coordinator_layout.fitsSystemWindows = true
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        a_main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    private fun hideSystemUi() {
        Timber.d("** hideSystemUi")
        a_main_drawer_layout.fitsSystemWindows = false
        a_main_coordinator_layout.fitsSystemWindows = false
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        a_main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    fun setupToolbar(toolbar: Toolbar, collapsingToolbarLayout: CollapsingToolbarLayout? = null) {
        setSupportActionBar(toolbar)

        // TODO: This will likely cause a memory leak and should be refactored so we control listeners and not NavigationUI
        if (collapsingToolbarLayout != null) {
            NavigationUI.setupWithNavController(collapsingToolbarLayout, toolbar, navController, a_main_drawer_layout)
        } else {
            NavigationUI.setupWithNavController(toolbar, navController, a_main_drawer_layout)
        }
        NavigationUI.setupWithNavController(a_main_nav_view, navController)
    }
}
