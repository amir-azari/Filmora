package azari.amirhossein.filmora.ui.preferences

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.adapter.MoviePreferencesAdapter
import azari.amirhossein.adapter.SearchMoviePreferencesAdapter
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.FragmentMoviePreferencesBinding
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.MoviePreferencesViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MoviePreferencesFragment : Fragment() {

    private var _binding: FragmentMoviePreferencesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var moviePreferencesAdapter: MoviePreferencesAdapter

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
        setupSearchItemClickListener()
    }

    private fun setupRecyclerViews() {
        binding.rvSelectedMovies.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = moviePreferencesAdapter
        }

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

    private fun setupSearchItemClickListener() {
        searchMovieAdapter.onItemClick = { selectedMovie ->
            val currentEmptyPosition = viewModel.selectedMovies.value?.indexOfFirst { it.id == -1 } ?: 0
            viewModel.updateSelectedMovie(selectedMovie, currentEmptyPosition)
            binding.actvSearch.setText("")
            binding.rvSearchResults.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        observeSearchResults()
        observeSelectedMovies()
        observeErrorMessage()
        setupGenresObserver()
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        val movieList = response.data?.results ?: emptyList()
                        searchMovieAdapter.differ.submitList(movieList)
                        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.rvSearchResults.adapter = searchMovieAdapter

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
    private fun setupGenresObserver() {
        viewModel.genres.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkRequest.Loading -> {
                }
                is NetworkRequest.Success -> {
                    setupGenresChipGroups(result.data?.genres.orEmpty())
                }
                is NetworkRequest.Error -> {
                    showErrorSnackbar(binding.root, result.message.toString())
                }
            }
        }
    }

    private fun setupGenresChipGroups(genres: List<ResponseGenresList.Genre?>) {
        binding.chipGroupFavoriteGenres.removeAllViews()
        binding.chipGroupDislikedGenres.removeAllViews()

        genres.forEach { genre ->
            genre?.let { addGenreChips(it) }
        }
    }

    private fun addGenreChips(genre: ResponseGenresList.Genre) {
        val favoriteChip = createChip(genre.name.toString())
        val dislikeChip = createChip(genre.name.toString())

        binding.chipGroupFavoriteGenres.addView(favoriteChip)
        binding.chipGroupDislikedGenres.addView(dislikeChip)

        setupChipListeners(favoriteChip, dislikeChip)
    }

    private fun createChip(text: String) = Chip(requireContext()).apply {
        this.text = text
        isCheckable = true
    }

    private fun setupChipListeners(favoriteChip: Chip, dislikeChip: Chip) {
        favoriteChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) dislikeChip.isChecked = false
        }

        dislikeChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) favoriteChip.isChecked = false
        }
    }

    private fun observeSelectedMovies() {
        viewModel.selectedMovies.observe(viewLifecycleOwner) { movies ->
            moviePreferencesAdapter.differ.submitList(movies)
        }
    }

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let { showWarningSnackbar(binding.root, it) }
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