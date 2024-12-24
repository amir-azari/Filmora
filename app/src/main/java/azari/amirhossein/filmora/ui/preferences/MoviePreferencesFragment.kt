package azari.amirhossein.filmora.ui.preferences

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.adapter.SearchMoviePreferencesAdapter
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.FragmentMoviePreferencesBinding
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.MoviePreferencesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MoviePreferencesFragment : Fragment() {

    private var _binding: FragmentMoviePreferencesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var searchMovieAdapter: SearchMoviePreferencesAdapter

    private val viewModel: MoviePreferencesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviePreferencesBinding.inflate(inflater, container, false)
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

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = searchMovieAdapter
        }
    }

    private fun setupSearchView() {
        binding.tilSearch.findViewById<AutoCompleteTextView>(R.id.actv_search).apply {
            setOnEditorActionListener { textView, actionId, _ ->
                handleSearchAction(textView.text.toString(), actionId)
            }

            doOnTextChanged { text, _, _, _ ->
                handleSearchTextChange(text?.toString())
            }
        }
    }

    private fun handleSearchAction(query: String, actionId: Int): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_SEARCH && query.isNotEmpty()) {
            viewModel.searchMovie(query)
            true
        } else {
            false
        }
    }

    private fun handleSearchTextChange(query: String?) {
        if (!query.isNullOrBlank() && query.length >= 2) {
            viewModel.searchMovie(query)
            binding.rvSearchResults.visibility = View.VISIBLE
        } else {
            binding.rvSearchResults.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        observeSearchResults()
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val movieList = response.data?.results ?: emptyList()
                        searchMovieAdapter.differ.submitList(movieList)
                        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.rvSearchResults.adapter = searchMovieAdapter

                    }
                    is NetworkRequest.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NetworkRequest.Error -> {
                        binding.progressBar.visibility = View.GONE

                        showErrorSnackbar(binding.root, response.message.toString())
                    }
                }
            }
        }
    }

    private fun showErrorSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.error, R.color.white, Gravity.TOP)
            show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}