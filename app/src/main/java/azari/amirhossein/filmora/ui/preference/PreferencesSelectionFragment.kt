package azari.amirhossein.filmora.ui.preference

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.models.GenreResponse.Genre
import azari.amirhossein.filmora.databinding.FragmentLoginBinding
import azari.amirhossein.filmora.databinding.FragmentPreferencesSelectionBinding
import azari.amirhossein.filmora.ui.authentication.AuthenticationViewModel
import azari.amirhossein.filmora.ui.preference.adapter.SearchAdapter
import azari.amirhossein.filmora.ui.preference.adapter.SelectedItemsAdapter
import azari.amirhossein.filmora.utils.view.UiText
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreferencesSelectionFragment : Fragment() {
    // ViewBinding
    private var _binding: FragmentPreferencesSelectionBinding? = null
    private val binding get() = _binding!!
    private var inflatedGenresView: View? = null

    // ViewModels
    private val viewModel: PreferencesViewModel by viewModels()
    private val authViewModel: AuthenticationViewModel by hiltNavGraphViewModels(R.id.auth_nav)

    // Adapters for search and selected items
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var selectedItemsAdapter: SelectedItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        _binding = FragmentPreferencesSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentSelectionType = viewModel.uiState.value.selectionType
                if (currentSelectionType == SelectionType.TV) {
                    findNavController().popBackStack()
                } else {
                    lifecycleScope.launch {
                        authViewModel.logout()

                        findNavController().navigate(R.id.action_preferencesSelectionFragment_to_loginFragment)
                    }
                }
            }
        })

        setupAdapters()    // Initialize adapters
        setupListeners()   // Setup UI listeners
        observeUiState()   // Observe state from ViewModel
    }

    // --- Initialize RecyclerView adapters ---
    private fun setupAdapters() {
        searchAdapter = SearchAdapter().apply {
            onItemClick = { item ->
                viewModel.onAddItem(item)
                binding.actvSearch.text?.clear()

            }
        }
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            setHasFixedSize(true)
        }

        selectedItemsAdapter = SelectedItemsAdapter().apply {
            onRemoveClick = { itemId ->
                val currentItems = viewModel.uiState.value.selectedItems
                val item = currentItems.find { it.id == itemId }
                if (item != null) {
                    viewModel.onRemoveItem(item)
                }
            }
        }

        binding.rvSelectedItems.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = selectedItemsAdapter
            setHasFixedSize(true)
        }
    }

    // --- Setup UI listeners ---
    private fun setupListeners() {
        binding.actvSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchQueryChanged(text.toString())
        }

        binding.btnNext.setOnClickListener {
            viewModel.onConfirmClicked()
        }
    }

    // --- Observe ViewModel state ---
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUi(state)
                }
            }
            launch {
                viewModel.searchQuery.collect { query ->
                    if (binding.actvSearch.text.toString() != query) {
                        binding.actvSearch.setText(query, false)
                    }
                }
            }
        }
    }

    // --- Update UI based on state ---
    private fun updateUi(state: PreferencesUiState) {
        binding.tvHeaderTitle.text = getString(state.headerTitleResId)
        binding.tvSelectionPrompt.text = getString(state.selectionPromptResId)
        binding.tilSearch.hint = getString(state.searchHintResId)
        binding.tvSelectedItemsTitle.text = getString(state.selectedItemsTitleResId)
        binding.btnNext.text = getString(state.buttonTextResId)

        binding.nextProgressbar.isVisible = state.isLoading
        binding.btnNext.isEnabled = !state.isLoading
        binding.progressBar.isVisible = state.isSearching
        binding.rvSearchResults.isVisible = !state.isSearching && state.searchResults.isNotEmpty()

         searchAdapter.submitList(state.searchResults)
         selectedItemsAdapter.submitList(state.selectedItems)

        if (binding.actvSearch.text.isNullOrEmpty()){
            binding.rvSearchResults.isVisible = false
        }

        // Inflate and show genre chips if loaded
        if (!state.isGenresLoading && state.genres.isNotEmpty()) {
            if (inflatedGenresView == null) {
                inflatedGenresView = binding.genresViewStub.inflate()
            }
            inflatedGenresView?.isVisible = true

            val favoriteChipGroup = inflatedGenresView?.findViewById<ChipGroup>(R.id.cg_favoriteGenres)
            val dislikedChipGroup = inflatedGenresView?.findViewById<ChipGroup>(R.id.cg_dislikedGenres)

            if (favoriteChipGroup != null && dislikedChipGroup != null) {
                updateGenreChips(favoriteChipGroup, state.genres, state.selectedFavoriteGenreIds) { genreId, isSelected ->
                    viewModel.onFavoriteGenreChanged(genreId, isSelected)
                }
                updateGenreChips(dislikedChipGroup, state.genres, state.selectedDislikedGenreIds) { genreId, isSelected ->
                    viewModel.onDislikedGenreChanged(genreId, isSelected)
                }
            }
        } else {
            inflatedGenresView?.isVisible = false
        }

        handleEvents(state)   // Show messages & navigate
    }

    // --- Update chips in ChipGroup ---
    private fun updateGenreChips(
        chipGroup: ChipGroup,
        allGenres: List<Genre>,
        selectedGenreIds: Set<Int>,
        onChipChecked: (Int, Boolean) -> Unit
    ) {
        if (chipGroup.childCount == 0 && allGenres.isNotEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(300)
                allGenres.forEach { genre ->
                    val chip = Chip(context).apply {
                        text = genre.name
                        isCheckable = true
                        tag = genre.id
                        setOnCheckedChangeListener { _, isChecked ->
                            onChipChecked(tag as Int, isChecked)
                        }
                    }
                    chipGroup.addView(chip)
                }
                // Set selected chips
                for (i in 0 until chipGroup.childCount) {
                    val chip = chipGroup.getChildAt(i) as Chip
                    chip.isChecked = selectedGenreIds.contains(chip.tag as Int)
                }
            }
        } else {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                chip.isChecked = selectedGenreIds.contains(chip.tag as Int)
            }
        }
    }

    // --- Handle navigation and messages ---
    private fun handleEvents(state: PreferencesUiState) {
        state.userMessage ?.let { event ->
            event.getContentIfNotHandled()?.let { uiText ->
                val message = when (uiText) {
                    is UiText.DynamicString -> uiText.value
                    is UiText.StringResource -> getString(uiText.id)
                }
                if (message.isNotEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            }
        }

        state.navigationEvent?.getContentIfNotHandled()?.let { destination ->
            when (destination) {
                is PreferencesNavigation .ToTvPreferences -> {
                    val action = PreferencesSelectionFragmentDirections.actionMovieSelectionToTvShowSelection()
                    findNavController().navigate(action)
                }
                is PreferencesNavigation .ToMainApp -> {
                    findNavController().navigate(R.id.action_global_to_bottom_home)

                }
            }
        }
    }

    override fun onDestroyView() {
        inflatedGenresView = null
        super.onDestroyView()
        _binding = null
    }
}