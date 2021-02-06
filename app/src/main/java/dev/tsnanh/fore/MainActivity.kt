package dev.tsnanh.fore

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.isVisible = when (destination.id) {
                R.id.navigation_add_food -> false
                else -> true
            }
        }
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}