package azari.amirhossein.filmora.ui.preferences

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.MoviePreferencesAdapter
import azari.amirhossein.filmora.adapter.SearchMoviePreferencesAdapter
import azari.amirhossein.filmora.adapter.SearchTvPreferencesAdapter
import azari.amirhossein.filmora.adapter.TvPreferencesAdapter
import azari.amirhossein.filmora.databinding.FragmentTvPreferencesBinding
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.MoviePreferencesViewModel
import azari.amirhossein.filmora.viewmodel.TvPreferencesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TvPreferencesFragment : Fragment() {
    //Binding
    private var _binding: FragmentTvPreferencesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tvPreferencesAdapter: TvPreferencesAdapter

    @Inject
    lateinit var searchTvAdapter: SearchTvPreferencesAdapter
    private val viewModel: TvPreferencesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTvPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupSearchView()
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        binding.rvSelectedSeries.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = tvPreferencesAdapter.apply {
                onRemoveClick = { position ->
                    viewModel.removeSelectedSerial(position)
                }
            }
        }

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = searchTvAdapter.apply {
                onItemClick = { movie ->
                    viewModel.addSelectedSerial(movie)
                    binding.actvSearch.setText("")
                    binding.rvSearchResults.visibility = View.GONE
                }
            }
        }
    }

    private fun setupSearchView() {
        binding.actvSearch.apply {
            setOnEditorActionListener { textView, actionId, _ ->
                handleSearchAction(textView.text.toString(), actionId)
            }
            doOnTextChanged { text, _, _, _ ->
                handleSearchTextChange(text?.toString())
            }
        }
    }

    private fun handleSearchAction(query: String, actionId: Int): Boolean {
        val isSearchValid = actionId == EditorInfo.IME_ACTION_SEARCH && query.isNotEmpty()
        binding.rvSearchResults.visibility = if (isSearchValid) View.VISIBLE else View.GONE

        if (isSearchValid) {
            viewModel.searchSeres(query)
        }
        return isSearchValid
    }


    private fun handleSearchTextChange(query: String?) {
        if (!query.isNullOrBlank() && query.length >= 2) {
            viewModel.searchSeres(query)
            binding.rvSearchResults.visibility = View.VISIBLE
        } else {
            binding.rvSearchResults.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        observeSearchResults()
        observeSelectedSeries()
        observeErrorMessage()
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        val movieList = response.data?.results ?: emptyList()
                        searchTvAdapter.differ.submitList(movieList)


                    }

                    is NetworkRequest.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is NetworkRequest.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE

                        showErrorSnackbar(binding.root, response.message.toString())
                    }
                }
            }
        }
    }
    private fun observeSelectedSeries() {
        viewModel.selectedSeries.observe(viewLifecycleOwner) { movies ->
            tvPreferencesAdapter.differ.submitList(movies)
        }
    }

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { message ->
                showWarningSnackbar(binding.root, message)
            }
        }
    }

    private fun showErrorSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.error, R.color.white, Gravity.TOP)
            show()
        }
    }

    private fun showWarningSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.warning, R.color.cardBackgroundColor, Gravity.TOP)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}