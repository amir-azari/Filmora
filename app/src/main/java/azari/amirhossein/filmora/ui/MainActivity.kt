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
import azari.amirhossein.filmora.ui.home.HomeFragment
import azari.amirhossein.filmora.ui.movies.MovieFragment
import azari.amirhossein.filmora.ui.tvs.TvFragment
import azari.amirhossein.filmora.ui.people.PeopleFragment
import androidx.fragment.app.Fragment
import azari.amirhossein.filmora.ui.MainFragmentFactory
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

    // Real fragment instances for show/hide tab caching
    private val homeFragment by lazy { HomeFragment() }
    private val movieFragment by lazy { MovieFragment() }
    private val tvFragment by lazy { TvFragment() }
    private val peopleFragment by lazy { PeopleFragment() }
    private var activeFragment: Fragment = homeFragment

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
        navHost.childFragmentManager.fragmentFactory = MainFragmentFactory()
        navController = navHost.navController
        navController.setGraph(R.navigation.nav_main)

        // Set NavController on tab_container so that findNavController() inside child fragments works
        androidx.navigation.Navigation.setViewNavController(binding.tabContainer, navController)

        // Initialize real fragments inside tab_container
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.tab_container, homeFragment, "home").show(homeFragment)
                add(R.id.tab_container, movieFragment, "movie").hide(movieFragment)
                add(R.id.tab_container, tvFragment, "tv").hide(tvFragment)
                add(R.id.tab_container, peopleFragment, "people").hide(peopleFragment)
                commit()
            }
            activeFragment = homeFragment
        } else {
            activeFragment = supportFragmentManager.findFragmentByTag("home") ?: homeFragment
        }

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

        // Setup Bottom Navigation Listener manually to sync with custom show/hide caching
        binding.bottomNav.setOnItemSelectedListener { item ->
            val targetFragment = when (item.itemId) {
                R.id.homeFragment -> supportFragmentManager.findFragmentByTag("home") ?: homeFragment
                R.id.movieFragment -> supportFragmentManager.findFragmentByTag("movie") ?: movieFragment
                R.id.tvFragment -> supportFragmentManager.findFragmentByTag("tv") ?: tvFragment
                R.id.peopleFragment -> supportFragmentManager.findFragmentByTag("people") ?: peopleFragment
                else -> null
            }
            if (targetFragment != null) {
                // If we are currently showing a detail/sub screen in nav_host_fragment, pop back to start destination
                if (navController.currentDestination?.id != navController.graph.startDestinationId &&
                    navController.currentDestination?.id != R.id.homeFragment &&
                    navController.currentDestination?.id != R.id.movieFragment &&
                    navController.currentDestination?.id != R.id.tvFragment &&
                    navController.currentDestination?.id != R.id.peopleFragment) {
                    navController.popBackStack(navController.graph.startDestinationId, false)
                }

                // Navigate in NavController to keep state in sync
                if (navController.currentDestination?.id != item.itemId) {
                    navController.navigate(item.itemId)
                }

                // Toggle visibilities
                binding.tabContainer.visibility = View.VISIBLE
                binding.navHostFragment.visibility = View.INVISIBLE

                if (activeFragment != targetFragment) {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(targetFragment)
                        .commit()
                    activeFragment = targetFragment
                }
                handleDestinationChanges(item.itemId)
                true
            } else {
                false
            }
        }

        observeAccountDetails()
        setupProfileClickListener()

        // Handle destination changes to manage dual-container visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val tabId = destination.id
            if (tabId in setOf(R.id.homeFragment, R.id.movieFragment, R.id.tvFragment, R.id.peopleFragment)) {
                binding.tabContainer.visibility = View.VISIBLE
                binding.navHostFragment.visibility = View.INVISIBLE
                showBottomNavWithFade()

                // Synchronize bottom_nav selection without triggering infinite loops
                if (binding.bottomNav.selectedItemId != tabId) {
                    binding.bottomNav.selectedItemId = tabId
                }

                val targetFragment = when (tabId) {
                    R.id.homeFragment -> supportFragmentManager.findFragmentByTag("home") ?: homeFragment
                    R.id.movieFragment -> supportFragmentManager.findFragmentByTag("movie") ?: movieFragment
                    R.id.tvFragment -> supportFragmentManager.findFragmentByTag("tv") ?: tvFragment
                    R.id.peopleFragment -> supportFragmentManager.findFragmentByTag("people") ?: peopleFragment
                    else -> homeFragment
                }
                if (activeFragment != targetFragment) {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(targetFragment)
                        .commit()
                    activeFragment = targetFragment
                }
                handleDestinationChanges(tabId)
            } else {
                // If navigating to detail screens, hide the tab container and show the nav host fragment
                binding.tabContainer.visibility = View.GONE
                binding.navHostFragment.visibility = View.VISIBLE
                handleDestinationChanges(tabId)
            }
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