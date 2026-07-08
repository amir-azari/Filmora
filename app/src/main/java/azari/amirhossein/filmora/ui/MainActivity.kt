package azari.amirhossein.filmora.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ActivityMainBinding

import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.SharedAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import azari.amirhossein.filmora.utils.Constants

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //Binding
    private lateinit var binding: ActivityMainBinding
    private val accountViewModel: SharedAccountViewModel by viewModels()

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

        // Setup Bottom Navigation with NavController (enables standard Multiple Back Stacks natively)
        binding.bottomNav.setupWithNavController(navController)

        observeAccountDetails()
        setupProfileClickListener()

        // Handle destination changes to manage toolbar and bottom navigation visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val tabId = destination.id
            if (tabId in setOf(R.id.homeFragment, R.id.movieFragment, R.id.tvFragment, R.id.peopleFragment)) {
                showBottomNavWithFade()
            } else {
                hideBottomNavWithFade()
            }
            handleDestinationChanges(tabId)
        }

    }

    private fun handleDestinationChanges(destinationId: Int) {
        val hiddenFragments = listOf(
            R.id.movieDetailFragment,
            R.id.tvDetailsFragment,
            R.id.mayLikeMoviesFragment,
            R.id.mayLikeTvsFragment,
            R.id.movieSectionFragment,
            R.id.tvSectionFragment,
            R.id.peopleSectionFragment,
            R.id.peopleDetailFragment,
            R.id.creditsFragment,
            R.id.collectionFragment,
            R.id.castAndCrewFragment,
            R.id.reviewsFragment,
            R.id.profileFragment,
            R.id.favoriteFragment,
            R.id.watchlistFragment,
            R.id.ratedFragment,
            R.id.searchFragment,
        )
        val isMenuHidden = destinationId in hiddenFragments
        for (i in 0 until binding.toolbar.menu.size()) {
            binding.toolbar.menu.getItem(i).isVisible = !isMenuHidden
        }

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

            R.id.movieDetailFragment,
            R.id.tvDetailsFragment,
            R.id.mayLikeMoviesFragment,
            R.id.mayLikeTvsFragment,
            R.id.movieSectionFragment,
            R.id.tvSectionFragment,
            R.id.peopleSectionFragment,
            R.id.peopleDetailFragment,
            R.id.searchFragment,
            R.id.profileFragment -> {

                hideBottomNavWithFade()
                showToolbar()
                hideProfileSection()
                showToolbarTitle()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }

            R.id.splashFragment, R.id.loginFragment, R.id.moviePreferencesFragment,
            R.id.tvPreferencesFragment, R.id.webViewFragment,
                -> {
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
        val hiddenFragments = listOf(
            R.id.movieDetailFragment,
            R.id.tvDetailsFragment,
            R.id.mayLikeMoviesFragment,
            R.id.mayLikeTvsFragment,
            R.id.movieSectionFragment,
            R.id.tvSectionFragment,
            R.id.peopleSectionFragment,
            R.id.peopleDetailFragment,
            R.id.creditsFragment,
            R.id.collectionFragment,
            R.id.castAndCrewFragment,
            R.id.reviewsFragment,
            R.id.profileFragment,
            R.id.favoriteFragment,
            R.id.watchlistFragment,
            R.id.ratedFragment,
            R.id.searchFragment,
        )
        val currentDestinationId = navController.currentDestination?.id

        for (i in 0 until menu.size()) {
            menu.getItem(i).isVisible = currentDestinationId !in hiddenFragments
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                navController.navigate(R.id.actionToSearchFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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
        binding.bottomNav.visibility = View.GONE
        binding.bottomNav.alpha = 1f
    }


    private fun showBottomNavWithFade() {
        binding.bottomNav.visibility = View.VISIBLE
        binding.bottomNav.alpha = 1f
    }

    private fun observeAccountDetails() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                accountViewModel.accountDetails.collect { state ->
                    when (state) {
                        is NetworkRequest.Success -> {
                            state.data?.let { account ->
                                binding.toolbar.findViewById<TextView>(R.id.tv_name).text =
                                    account.username

                                val avatar = account.avatar
                                val avatarUrl = when {
                                    !avatar?.tmdb?.avatarPath.isNullOrEmpty() -> {
                                        Constants.Network.IMAGE_BASE_URL + Constants.ImageSize.W500 + avatar.tmdb.avatarPath
                                    }
                                    !avatar?.gravatar?.hash.isNullOrEmpty() -> {
                                        "https://secure.gravatar.com/avatar/${avatar.gravatar?.hash}?s=200"
                                    }
                                    else -> null
                                }

                                if (!avatarUrl.isNullOrEmpty()) {
                                    binding.imgProfile.load(avatarUrl) {
                                        crossfade(true)
                                        placeholder(R.drawable.profile)
                                        error(R.drawable.profile)
                                    }
                                } else {
                                    binding.imgProfile.setImageResource(R.drawable.profile)
                                }
                            }
                        }

                        is NetworkRequest.Loading -> {
                            // After logout, reset to default guest UI
                            binding.toolbar.findViewById<TextView>(R.id.tv_name).text = ""
                            binding.imgProfile.setImageResource(R.drawable.profile)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupProfileClickListener() {
        binding.imgProfile.setClickAnimation {
            navController.navigate(R.id.actionToProfileFragment)

        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}