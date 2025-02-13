package azari.amirhossein.filmora.ui.preferences

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.MoviePreferencesAdapter
import azari.amirhossein.filmora.adapter.SearchMoviePreferencesAdapter
import azari.amirhossein.filmora.adapter.SearchTvPreferencesAdapter
import azari.amirhossein.filmora.adapter.TvPreferencesAdapter
import azari.amirhossein.filmora.databinding.FragmentTvPreferencesBinding
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.MoviePreferencesViewModel
import azari.amirhossein.filmora.viewmodel.TvPreferencesViewModel
import com.google.android.material.chip.Chip
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
        setupConfirmButton()
        setupBackButton()
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
        setupGenresObserver()
        observeSavePreferences()

    }
    private fun setupConfirmButton() {
        binding.btnConfirmed.setOnClickListener {
            if (!viewModel.isNetworkAvailable.value) {
                showErrorSnackbar(binding.root, Constants.Message.NO_INTERNET_CONNECTION)
                return@setOnClickListener
            }

            if (viewModel.validatePreferences()) {
                binding.btnConfirmed.isEnabled = false
                binding.confirmedProgressbar.visibility = View.VISIBLE
                viewModel.savePreferences()
            }
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setClickAnimation {
            findNavController().navigateUp()
        }
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
                if (message.contains(Constants.Message.NO_INTERNET_CONNECTION)){
                    showErrorSnackbar(binding.root, message)
                }else {
                    showWarningSnackbar(binding.root, message)
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
            genre?.let {
                val favoriteChip = createChip(it.name.toString(), it.id)
                val dislikeChip = createChip(it.name.toString(), it.id)

                favoriteChip.isChecked =
                    viewModel.selectedFavoriteGenres.value?.contains(it.id) == true
                dislikeChip.isChecked =
                    viewModel.selectedDislikedGenres.value?.contains(it.id) == true

                binding.chipGroupFavoriteGenres.addView(favoriteChip)
                binding.chipGroupDislikedGenres.addView(dislikeChip)

                setupChipListeners(favoriteChip, dislikeChip, it.id)
            }
        }
    }

    private fun createChip(text: String, id: Int) = Chip(requireContext()).apply {
        this.text = text
        this.id = id
        isCheckable = true
    }

    private fun setupChipListeners(favoriteChip: Chip, dislikeChip: Chip, genreId: Int) {
        favoriteChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) dislikeChip.isChecked = false
            viewModel.updateFavoriteGenre(genreId, isChecked)
        }

        dislikeChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) favoriteChip.isChecked = false
            viewModel.updateDislikedGenre(genreId, isChecked)
        }
    }
    private fun observeSavePreferences() {
        viewModel.savePreferencesResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkRequest.Loading -> {
                    binding.confirmedProgressbar.visibility = View.VISIBLE
                    binding.btnConfirmed.visibility = View.INVISIBLE
                }
                is NetworkRequest.Success -> {
                    binding.confirmedProgressbar.visibility = View.GONE
                    binding.btnConfirmed.visibility = View.VISIBLE
                    findNavController().navigate(R.id.actionTvPreferencesToHome)
                }
                is NetworkRequest.Error -> {
                    binding.confirmedProgressbar.visibility = View.GONE
                    binding.btnConfirmed.visibility = View.VISIBLE
                    showErrorSnackbar(binding.root, result.message!!)
                }
            }
        }
    }
    private fun showErrorSnackbar(root: View, message: String) {
        if (message == Constants.Message.NO_INTERNET_CONNECTION) {
            Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE).apply {
                setActionTextColor(ContextCompat.getColor(root.context, R.color.white))
                setAction("Retry") {
                    viewModel.fetchRetry()
                }
                customize(R.color.error, R.color.white, Gravity.TOP)
                show()
            }
        }else {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
                customize(R.color.error, R.color.white, Gravity.TOP)
                show()
            }
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