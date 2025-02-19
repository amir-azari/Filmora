package azari.amirhossein.filmora.ui.search

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.SearchPagerAdapter
import azari.amirhossein.filmora.databinding.FragmentSearchBinding
import azari.amirhossein.filmora.models.search.SearchResultPageData
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private var searchAdapter: SearchPagerAdapter? = null
    private var isSearchCompleted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        observeTabState()
        setupMenu()
    }

    private fun initializeViews() {
        with(binding) {
            viewPager.visibility = View.GONE
            tlSearch.visibility = View.GONE
            txtNotFound.visibility = View.GONE
            txtHint.visibility = View.VISIBLE
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.search_menu, menu)
                setupSearchMenuItem(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setupSearchMenuItem(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search_view)
        val searchView = searchItem.actionView as SearchView
        setupSearchView(searchView)
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.apply {
            queryHint = getString(R.string.search_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    handleSearchQuery(query)

                    // Close the keyboard
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(searchView.windowToken, 0)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }
    }

    private fun handleSearchQuery(query: String?) {
        query?.takeIf { it.isNotBlank() }?.let {
            viewModel.search(it)
            isSearchCompleted = true
        }
    }



    private fun showNoResultsState() {
        with(binding) {
            viewPager.visibility = View.INVISIBLE
            tlSearch.visibility = View.INVISIBLE
            txtNotFound.visibility = View.VISIBLE
        }
    }

    private fun setupViewPagerWithTabs(visibleTabs: List<Int>) {
        searchAdapter = SearchPagerAdapter(this, visibleTabs).also { adapter ->
            binding.viewPager.apply {
                this.adapter = adapter
                offscreenPageLimit = 1
                setPageTransformer { page, position ->
                    page.alpha = 1 - kotlin.math.abs(position)
                }
                // currentTabPosition
                setCurrentItem(viewModel.currentTabPosition.value, false)
            }

            TabLayoutMediator(binding.tlSearch, binding.viewPager) { tab, position ->
                tab.text = getTabTitle(visibleTabs[position])
            }.attach()

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.updateTabPosition(position)
                }
            })
        }
    }


    private fun getTabTitle(tabIndex: Int) = when (tabIndex) {
        0 -> "All"
        1 -> getString(R.string.movies)
        2 -> getString(R.string.tv_shows)
        3 -> getString(R.string.celebrities)
        else -> ""
    }

    private fun observeTabState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe visible tabs changes
                launch {
                    viewModel.visibleTabs.collectLatest { tabs ->
                        if (tabs.isNotEmpty()) {
                            setupViewPagerWithTabs(tabs)
                        }
                    }
                }

                // Observe search results
                launch {
                    viewModel.searchAllResults.collectLatest { result ->
                        if (result != null) {
                            handleSearchResult(result)
                        }
                    }
                }
            }
        }
    }

    private fun handleSearchResult(result: NetworkRequest<SearchResultPageData>) {
        when (result) {
            is NetworkRequest.Loading -> showLoading()
            is NetworkRequest.Success -> {
                success()
                result.data?.let { data ->
                    // handle tab
                    viewModel.updateAllSearchResults(data)
                    updateUIVisibility(data)
                }
            }
            is NetworkRequest.Error -> handleError(result.message.toString())
        }
    }
    private fun updateUIVisibility(data: SearchResultPageData) {
        val hasAnyResults = !data.movies?.results.isNullOrEmpty() ||
                !data.tvShows?.results.isNullOrEmpty() ||
                !data.people?.results.isNullOrEmpty()

        if (!hasAnyResults) {
            showNoResultsState()
            return
        }

        with(binding) {
            viewPager.visibility = View.VISIBLE
            tlSearch.visibility = View.VISIBLE
            txtNotFound.visibility = View.GONE
            txtHint.visibility = View.GONE
        }
    }

    private fun handleError(message: String) {
        showError()
        showErrorSnackbar(binding.root, message)
    }

    private fun showErrorSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.error, R.color.white, Gravity.TOP)
            show()
        }
    }

    private fun success() {
        with(binding) {
            txtHint.visibility = View.GONE
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun showError() {
        with(binding) {
            progressBar.visibility = View.INVISIBLE
            viewPager.visibility = View.INVISIBLE
            tlSearch.visibility = View.INVISIBLE
            txtHint.visibility = View.VISIBLE
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchAdapter = null
        _binding = null
    }
}