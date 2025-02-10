package azari.amirhossein.filmora.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ActivityMainBinding
import azari.amirhossein.filmora.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //Binding
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var navHost: NavHostFragment
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
        navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                ?: NavHostFragment.create(R.navigation.nav_main).also {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, it)
                        .setReorderingAllowed(true)
                        .commitNow()
                }
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
            handleDestinationChanges(destination.id)
        }
    }

    private fun handleDestinationChanges(destinationId: Int) {
        when (destinationId) {
            R.id.homeFragment -> {
                showBottomNavWithFade()
                showToolbar()
                showProfileSection()
                hideToolbarTitle()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }

            R.id.movieFragment, R.id.tvFragment, R.id.peopleFragment -> {
                showBottomNavWithFade()
                showToolbar()
                hideProfileSection()
                showToolbarTitle()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }

            R.id.movieDetailFragment, R.id.tvDetailsFragment , R.id.mayLikeMoviesFragment , R.id.mayLikeTvsFragment ,R.id.movieSectionFragment-> {
                hideBottomNavWithFade()
                showToolbar()
                hideProfileSection()
                showToolbarTitle()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }

            R.id.splashFragment, R.id.loginFragment, R.id.moviePreferencesFragment,
            R.id.tvPreferencesFragment, R.id.webViewFragment -> {
                hideBottomNav()
                hideToolbar()
            }
        }
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val hiddenFragments = listOf(R.id.movieDetailFragment, R.id.tvDetailsFragment ,  R.id.mayLikeMoviesFragment , R.id.mayLikeTvsFragment , R.id.movieSectionFragment)
        val currentDestinationId = navController.currentDestination?.id

        for (i in 0 until menu.size()) {
            menu.getItem(i).isVisible = currentDestinationId !in hiddenFragments
        }
        return super.onPrepareOptionsMenu(menu)
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

    private fun hideBottomNavWithFade() {
        if (binding.bottomNav.visibility != View.GONE && binding.bottomNav.alpha == 1f) {
            ObjectAnimator.ofFloat(binding.bottomNav, "alpha", 1f, 0f).apply {
                duration = 150
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.bottomNav.visibility = View.GONE
                        binding.bottomNav.alpha = 1f
                    }
                })
                start()
            }
        }
    }


    private fun showBottomNavWithFade() {
        if (binding.bottomNav.visibility != View.VISIBLE) {

            binding.bottomNav.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(binding.bottomNav, "alpha", 0f, 1f).apply {
                duration = 150
                start()
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}