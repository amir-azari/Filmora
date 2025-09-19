package azari.amirhossein.filmora.ui.preference

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.models.movie.MovieResponse
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.models.tv.TvShowResponse
import azari.amirhossein.filmora.domain.repository.ContentRepository
import azari.amirhossein.filmora.domain.usecase.SavePreferencesUseCase
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.Event
import azari.amirhossein.filmora.utils.network.Resource
import azari.amirhossein.filmora.utils.view.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val savePreferencesUseCase: SavePreferencesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

    // --- Search query state ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    init {
        // Determine selection type from nav args (movie or tv)
        val typeArg = savedStateHandle.get<String>(Constants.Args.SELECTION_TYPE) ?: "movie"
        val selectionType = if (typeArg == "tv") SelectionType.TV else SelectionType.MOVIE

        _uiState.update { currentState -> updateStateForSelectionType(currentState, selectionType) }

        loadInitialData(selectionType)   // Load genres
        observeSearchQuery(selectionType) // Start observing search query
    }

    // --- Load initial genres for movie or TV ---
    private fun loadInitialData(selectionType: SelectionType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenresLoading = true) }
            _uiState.update { it.copy(isLoading = true) }

            val genresFlow = when (selectionType) {
                SelectionType.MOVIE -> contentRepository.getMovieGenres()
                SelectionType.TV -> contentRepository.getTvGenres()
            }
            genresFlow.collect { resource ->
                when (resource) {
                    is Resource.Success -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            isGenresLoading = false,
                            genres = resource.data.genres
                        )
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            isGenresLoading = false,
                            userMessage  = Event(UiText.StringResource(R.string.error_load_genres))
                        )
                    }
                    Resource.Loading -> {}
                }
            }
        }
    }

    // --- Update UI texts based on selection type ---
    private fun updateStateForSelectionType(
        currentState: PreferencesUiState,
        selectionType: SelectionType
    ): PreferencesUiState {
        return when (selectionType) {
            SelectionType.MOVIE -> currentState.copy(
                selectionType = selectionType,
                headerTitleResId = R.string.your_movie_taste,
                selectionPromptResId = R.string.choose_your_favorite_movies,
                searchHintResId = R.string.search_movies,
                selectedItemsTitleResId = R.string.selected_movies,
                buttonTextResId = R.string.next
            )
            SelectionType.TV -> currentState.copy(
                selectionType = selectionType,
                headerTitleResId = R.string.your_series_taste,
                selectionPromptResId = R.string.choose_your_favorite_series,
                searchHintResId = R.string.search_series,
                selectedItemsTitleResId = R.string.selected_series,
                buttonTextResId = R.string.confirmed
            )
        }
    }

    // --- Observe search input and fetch results ---
    private fun observeSearchQuery(selectionType: SelectionType) {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(Resource.Success(null))
                    }else {
                        _uiState.update { it.copy(isSearching = true) }
                        when (selectionType) {
                            SelectionType.MOVIE -> contentRepository.searchMovies(query)
                            SelectionType.TV -> contentRepository.searchTvShows(query)
                        }
                    }
                }
                .collect { resource ->
                    val searchResults = when (resource) {
                        is Resource.Success -> {
                            when (val data = resource.data) {
                                is MovieResponse -> {
                                    data.results.map { item ->
                                        ContentItem(item.id, item.title, item.posterPath, item.genreIds , item.releaseDate , item.voteAverage)
                                    }
                                }
                                is TvShowResponse -> {
                                    data.results.map { item ->
                                        ContentItem(item.id, item.name, item.posterPath, item.genreIds ,item.firstAirDate , item.voteAverage )
                                    }
                                }
                                else -> emptyList()
                            }
                        }
                        else -> emptyList()
                    }
                    _uiState.update { it.copy(isSearching = false, searchResults = searchResults) }
                }
        }
    }

    // --- Update search query value ---
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // --- Add item to selected list ---
    fun onAddItem(item: ContentItem) {
        val currentSelected = _uiState.value.selectedItems
        if (currentSelected.size >= 5) {
            _uiState.update { it.copy(userMessage  = Event(UiText.StringResource(R.string.error_select_limit))) }
            return
        }
        if (currentSelected.any { it.id == item.id }) {
            _uiState.update { it.copy(userMessage  = Event(UiText.StringResource(R.string.error_already_selected))) }
            return
        }
        _uiState.update { it.copy(selectedItems = currentSelected + item ,searchResults = emptyList()) }

        _searchQuery.value = ""

    }

    // --- Remove item from selected list ---
    fun onRemoveItem(item: ContentItem) {
        _uiState.update {
            it.copy(selectedItems = it.selectedItems.filterNot { selected -> selected.id == item.id })
        }
    }

    // --- Update favorite genres ---
    fun onFavoriteGenreChanged(genreId: Int, isSelected: Boolean) {
        val favorite = _uiState.value.selectedFavoriteGenreIds.toMutableSet()
        val disliked = _uiState.value.selectedDislikedGenreIds.toMutableSet()
        if (isSelected) {
            favorite.add(genreId)
            disliked.remove(genreId)
        } else {
            favorite.remove(genreId)
        }
        _uiState.update { it.copy(selectedFavoriteGenreIds = favorite, selectedDislikedGenreIds = disliked) }
    }

    // --- Update disliked genres ---
    fun onDislikedGenreChanged(genreId: Int, isSelected: Boolean) {
        val favorite = _uiState.value.selectedFavoriteGenreIds.toMutableSet()
        val disliked = _uiState.value.selectedDislikedGenreIds.toMutableSet()
        if (isSelected) {
            disliked.add(genreId)
            favorite.remove(genreId)
        } else {
            disliked.remove(genreId)
        }
        _uiState.update { it.copy(selectedFavoriteGenreIds = favorite, selectedDislikedGenreIds = disliked) }
    }

    // --- Confirm selection and navigate ---
    fun onConfirmClicked() {
        if (!validatePreferences()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentState = _uiState.value

            savePreferencesUseCase(
                type = currentState.selectionType,
                selectedItems = currentState.selectedItems,
                favoriteGenreIds = currentState.selectedFavoriteGenreIds,
                dislikedGenreIds = currentState.selectedDislikedGenreIds
            )

            _uiState.update { it.copy(isLoading = false) }

            val destination = if (currentState.selectionType == SelectionType.MOVIE) {
                PreferencesNavigation .ToTvPreferences
            } else {
                PreferencesNavigation .ToMainApp
            }
            _uiState.update { it.copy(navigationEvent = Event(destination)) }
        }
    }

    // --- Validate user selections ---
    private fun validatePreferences(): Boolean {
        val state = _uiState.value
        if (state.selectedItems.size < 5) {
            _uiState.update { it.copy(userMessage  = Event(UiText.StringResource(R.string.error_select_limit))) }
            return false
        }
        if (state.selectedFavoriteGenreIds.isEmpty() || state.selectedDislikedGenreIds.isEmpty()) {
            _uiState.update { it.copy(userMessage  = Event(UiText.StringResource(R.string.error_genre_preferences))) }
            return false
        }
        return true
    }
}
