package com.example.openlog

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        //Sets below fragments as "home" so "back" button doesnt show in top left corner
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.share_log_item_fragment,
                R.id.add_log_item_fragment,
                R.id.previous_logs_fragment
            )
            .build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)

        // Don't show bottom nav on Auth screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav);
            when (destination.id) {
                R.id.authFragment, R.id.loginFragment, R.id.registerFragment -> bottomNav.visibility = View.GONE
                else -> bottomNav.visibility = View.VISIBLE
            }
        }
    }

    //Navigation using "Back button"
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}