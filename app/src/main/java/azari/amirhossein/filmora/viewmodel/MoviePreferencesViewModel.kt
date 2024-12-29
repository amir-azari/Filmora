package azari.amirhossein.filmora.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.repository.MoviePreferencesRepository
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Event
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviePreferencesViewModel @Inject constructor(
    private val repository: MoviePreferencesRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _searchResult = MutableLiveData<NetworkRequest<ResponseMoviesList>>()
    val searchResult: LiveData<NetworkRequest<ResponseMoviesList>> = _searchResult

    private val _selectedMovies = MutableLiveData<List<ResponseMoviesList.Result>>()
    val selectedMovies: LiveData<List<ResponseMoviesList.Result>> = _selectedMovies

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _genres = MutableLiveData<NetworkRequest<ResponseGenresList>>()
    val genres: LiveData<NetworkRequest<ResponseGenresList>> = _genres

    private val _selectedFavoriteGenres = MutableLiveData<Set<Int>>(setOf())
    val selectedFavoriteGenres: LiveData<Set<Int>> = _selectedFavoriteGenres

    private val _selectedDislikedGenres = MutableLiveData<Set<Int>>(setOf())
    val selectedDislikedGenres: LiveData<Set<Int>> = _selectedDislikedGenres

    init {
        setupSearchQueryFlow()
        fetchMovieGenres()

    }

    fun addSelectedMovie(movie: ResponseMoviesList.Result) {
        val currentList = _selectedMovies.value?.toMutableList() ?: mutableListOf()
        if (currentList.size < 5 && !currentList.contains(movie)) {
            currentList.add(movie)
            _selectedMovies.value = currentList
        } else if (currentList.size >= 5) {
            _errorMessage.value = Event("You can only select up to 5 movies")
        } else {
            _errorMessage.value = Event("This movie has already been selected")
        }
    }

    private fun setupSearchQueryFlow() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .flatMapLatest { query -> repository.searchMovies(query) }
                .collect { _searchResult.postValue(it) }
        }
    }

    fun searchMovie(query: String) {
        _searchQuery.value = query
    }


    private fun fetchMovieGenres() {
        viewModelScope.launch {
            repository.getMovieGenres()
                .collect { result ->
                    _genres.postValue(result)
                }
        }
    }
    fun removeSelectedMovie(position: Int) {
        val currentList = _selectedMovies.value?.toMutableList() ?: mutableListOf()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            _selectedMovies.value = currentList
        }
    }
    fun updateFavoriteGenre(genreId: Int, isSelected: Boolean) {
        val currentFavorites = _selectedFavoriteGenres.value?.toMutableSet() ?: mutableSetOf()
        val currentDislikes = _selectedDislikedGenres.value?.toMutableSet() ?: mutableSetOf()

        if (isSelected) {
            currentFavorites.add(genreId)
            currentDislikes.remove(genreId)
        } else {
            currentFavorites.remove(genreId)
        }

        _selectedFavoriteGenres.value = currentFavorites
        _selectedDislikedGenres.value = currentDislikes
    }

    fun updateDislikedGenre(genreId: Int, isSelected: Boolean) {
        val currentFavorites = _selectedFavoriteGenres.value?.toMutableSet() ?: mutableSetOf()
        val currentDislikes = _selectedDislikedGenres.value?.toMutableSet() ?: mutableSetOf()

        if (isSelected) {
            currentDislikes.add(genreId)
            currentFavorites.remove(genreId)
        } else {
            currentDislikes.remove(genreId)
        }

        _selectedFavoriteGenres.value = currentFavorites
        _selectedDislikedGenres.value = currentDislikes
    }
    private suspend fun fetchMovieKeywords(movieId: String): Set<Int> {
        var keywordIds = emptySet<Int>()
        repository.getMovieKeywords(movieId).collect { result ->
            when (result) {
                is NetworkRequest.Success -> {
                    keywordIds = result.data?.keywords?.mapNotNull { it?.id }?.toSet() ?: emptySet()
                }
                else -> {}
            }
        }
        return keywordIds
    }

    fun savePreferences() = viewModelScope.launch {
        try {
            val selectedMovies = _selectedMovies.value ?: emptyList()

            val allKeywords = mutableSetOf<Int>()
            selectedMovies.forEach { movie ->
                movie.id?.let { movieId ->
                    val movieKeywords = fetchMovieKeywords(movieId.toString())
                    allKeywords.addAll(movieKeywords)
                }
            }

            val preferences = TvAndMoviePreferences(
                selectedIds = selectedMovies.mapNotNull { it.id },
                favoriteGenres = _selectedFavoriteGenres.value ?: emptySet(),
                dislikedGenres = _selectedDislikedGenres.value ?: emptySet(),
                selectedKeywords = allKeywords,
                selectedGenres = selectedMovies.flatMap { it.genreIds ?: emptyList() }.filterNotNull().toSet()
            )

            sessionManager.saveMoviePreferences(preferences)
        } catch (e: Exception) {
            Log.e("Keywords", "Error saving preferences: ${e.message}")
        }
    }

    fun validatePreferences(): Boolean {
        val selectedMovies = _selectedMovies.value ?: emptyList()
        val favoriteGenres = _selectedFavoriteGenres.value ?: emptySet()
        val dislikedGenres = _selectedDislikedGenres.value ?: emptySet()

        if (selectedMovies.size < 5) {
            _errorMessage.value = Event("Please select exactly 5 movies")
            return false
        }

        if (favoriteGenres.isEmpty() && dislikedGenres.isEmpty()) {
            _errorMessage.value = Event("Please select at least one genre preference")
            return false
        }

        return true
    }
}