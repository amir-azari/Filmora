package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.MoviePreferencesRepository
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _searchResult = MutableLiveData<NetworkRequest<ResponseMoviesList>>()
    val searchResult: LiveData<NetworkRequest<ResponseMoviesList>> = _searchResult

    private val _selectedMovies = MutableLiveData<List<ResponseMoviesList.Result>>()
    val selectedMovies: LiveData<List<ResponseMoviesList.Result>> = _selectedMovies

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _genres = MutableLiveData<NetworkRequest<ResponseGenresList>>()
    val genres: LiveData<NetworkRequest<ResponseGenresList>> = _genres

    private val _selectedFavoriteGenres = MutableLiveData<Set<Int>>(setOf())
    val selectedFavoriteGenres: LiveData<Set<Int>> = _selectedFavoriteGenres

    private val _selectedDislikedGenres = MutableLiveData<Set<Int>>(setOf())
    val selectedDislikedGenres: LiveData<Set<Int>> = _selectedDislikedGenres
    init {
        initializeSelectedMovies()
        setupSearchQueryFlow()
        fetchMovieGenres()
    }

    private fun initializeSelectedMovies() {
        _selectedMovies.value = List(5) { createEmptyMovie() }
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

    fun updateSelectedMovie(movie: ResponseMoviesList.Result, position: Int) {
        if (!isValidMovieSelection(movie, position)) return

        val currentList = _selectedMovies.value?.toMutableList() ?: MutableList(5) { createEmptyMovie() }
        currentList[position] = movie
        _selectedMovies.postValue(currentList)
    }

    private fun isValidMovieSelection(movie: ResponseMoviesList.Result, position: Int): Boolean {
        val currentList = _selectedMovies.value.orEmpty()

        when {
            currentList.none { it.id == -1 } -> {
                _errorMessage.postValue("Maximum selection limit reached (5 movies)")
                return false
            }
            currentList.any { it.id == movie.id && movie.id != -1 } -> {
                _errorMessage.postValue("This movie has already been selected")
                return false
            }
            position !in 0..4 -> return false
        }

        return true
    }

    private fun fetchMovieGenres() {
        viewModelScope.launch {
            repository.getMovieGenres()
                .collect { result ->
                    _genres.postValue(result)
                }
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


    private fun createEmptyMovie() = ResponseMoviesList.Result(
        adult = false,
        backdropPath = null,
        genreIds = emptyList(),
        id = -1,
        originalLanguage = "",
        originalTitle = "",
        overview = "",
        popularity = 0.0,
        posterPath = null,
        releaseDate = "",
        title = "",
        video = false,
        voteAverage = 0.0,
        voteCount = 0
    )
}