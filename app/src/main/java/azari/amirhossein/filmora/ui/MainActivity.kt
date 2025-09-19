package azari.amirhossein.filmora.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ActivityMainBinding
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.view.UIUtils
import azari.amirhossein.filmora.utils.extensions.applyBottomInsets
import azari.amirhossein.filmora.utils.extensions.applyHorizontalInsets
import azari.amirhossein.filmora.utils.extensions.applySystemBarInsets
import azari.amirhossein.filmora.utils.extensions.applyTopInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //Binding
    private lateinit var binding: ActivityMainBinding

    // Navigation
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // fullscreen layout
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()   // system bars
        setupNavigation()     // nav setup
        observeNavigationChanges() // nav listener
    }

    private fun setupNavigation() {
        // NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Define top-level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.moviesFragment,
                R.id.tvShowsFragment,
                R.id.celebritiesFragment
            )
        )

        // Setup Toolbar + BottomNav
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)
    }

    private fun setupWindowInsets() {
        // Apply padding for system bars
        binding.main.applySystemBarInsets(applyTop = true, applyBottom = true, consume = true)
        binding.toolbar.applyTopInsets()
        binding.bottomNav.applyBottomInsets()
        binding.navHostFragment.applyHorizontalInsets()

    }

    private fun configureToolbarForMainScreens(destinationId: Int) {
        showToolbar()
        binding.profileSection.visibility = View.VISIBLE

        // Configure toolbar based on screen
        when (destinationId) {
            R.id.homeFragment -> {
                binding.toolbar.title = ""
                binding.toolbar.menu.clear()
                binding.toolbar.inflateMenu(R.menu.toolbar_menu)
            }

            R.id.moviesFragment -> {
                binding.toolbar.title = Constants.UI.TOOLBAR_TITLE_MOVIES
                binding.profileSection.visibility = View.GONE

            }

            R.id.tvShowsFragment -> {
                binding.toolbar.title = Constants.UI.TOOLBAR_TITLE_TV_SHOWS
                binding.profileSection.visibility = View.GONE
            }

            R.id.celebritiesFragment -> {
                binding.toolbar.title = Constants.UI.TOOLBAR_TITLE_CELEBRITIES
                binding.profileSection.visibility = View.GONE

            }
        }
    }

    private fun observeNavigationChanges() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // show bottom nav & configure toolbar
                R.id.homeFragment,
                R.id.moviesFragment,
                R.id.tvShowsFragment,
                R.id.celebritiesFragment -> {
                    showBottomNavigation()
                    configureToolbarForMainScreens(destination.id)
                }
                // hide both
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.preferencesSelectionFragment, -> {
                    hideBottomNavigation()
                    hideToolbar()

                }

                // hide bottom nav
                else -> {
                    hideBottomNavigation()
                    showToolbar()


                }
            }
        }
    }

    private fun showBottomNavigation() {
        UIUtils.showBottomNavigation(binding.bottomNavContainer)
    }

    private fun hideBottomNavigation() {
        UIUtils.hideBottomNavigation(binding.bottomNavContainer)
    }

    private fun showToolbar() {
        UIUtils.showToolbar(binding.toolbarContainer)
    }

    private fun hideToolbar() {
        UIUtils.hideToolbar(binding.toolbarContainer)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle navigation up with NavController
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}