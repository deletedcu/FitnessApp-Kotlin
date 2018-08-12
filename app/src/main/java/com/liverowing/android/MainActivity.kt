package com.liverowing.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.a_main_nav_host)
    }

    override fun onBackPressed() {
        if (a_main_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            a_main_drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            a_main_drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    fun setupToolbar(toolbar: Toolbar, collapsingToolbarLayout: CollapsingToolbarLayout? = null) {
        setSupportActionBar(toolbar)
        if (collapsingToolbarLayout != null) {
            NavigationUI.setupWithNavController(collapsingToolbarLayout, toolbar, navController, a_main_drawer_layout)
        } else {
            NavigationUI.setupWithNavController(toolbar, navController, a_main_drawer_layout)
        }
        NavigationUI.setupWithNavController(a_main_nav_view, navController)
    }
}
