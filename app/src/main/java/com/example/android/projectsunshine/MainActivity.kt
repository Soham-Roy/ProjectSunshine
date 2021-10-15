package com.example.android.projectsunshine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.android.projectsunshine.databinding.MainActivityBinding
import com.example.android.projectsunshine.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var _bind: MainActivityBinding
    private val bind get() = _bind

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = MainActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSupportActionBar(bind.mainActivityToolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.mainFragment, R.id.startFragment
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}