package azari.amirhossein.filmora.ui

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //Binding
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var navHost : NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.movieFragment,
                R.id.tvFragment,
                R.id.peopleFragment
            )
        )
        // Setup ActionBar with NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup Bottom Navigation
        binding.bottomNav.setupWithNavController(navController)

        // Handle destination changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    showBottomNav()
                    showToolbar()
                    showProfileSection()
                    hideToolbarTitle()
                }
                R.id.movieFragment,
                R.id.tvFragment,
                R.id.peopleFragment -> {
                    showBottomNav()
                    showToolbar()
                    hideProfileSection()
                    showToolbarTitle()
                }
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.moviePreferencesFragment,
                R.id.tvPreferencesFragment,
                R.id.webViewFragment -> {
                    hideBottomNav()
                    hideToolbar()
                }
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    private fun showProfileSection() {
        binding.profileSection.visibility = View.VISIBLE
    }

    private fun hideProfileSection() {
        binding.profileSection.visibility = View.GONE
    }

    private fun hideToolbarTitle() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun showToolbarTitle() {
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }
    private fun hideToolbar() {
        binding.toolbar.visibility = View.GONE
    }

    private fun showToolbar() {
        binding.toolbar.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bottomNav.visibility = View.GONE
    }
    private fun showBottomNav() {
        binding.bottomNav.visibility = View.VISIBLE
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}