package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.repository.TvPreferencesRepository
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
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
class TvPreferencesViewModel @Inject constructor(
    private val repository: TvPreferencesRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _searchResult = MutableLiveData<NetworkRequest<ResponseTvsList>>()
    val searchResult: LiveData<NetworkRequest<ResponseTvsList>> = _searchResult

    private val _selectedSeries = MutableLiveData<List<ResponseTvsList.Result>>()
    val selectedSeries: LiveData<List<ResponseTvsList.Result>> = _selectedSeries

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
    private fun setupSearchQueryFlow() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .flatMapLatest { query -> repository.searchTv(query) }
                .collect { _searchResult.postValue(it) }
        }
    }

    fun searchSeres(query: String) {
        _searchQuery.value = query
    }

    fun addSelectedSerial(movie: ResponseTvsList.Result) {
        val currentList = _selectedSeries.value?.toMutableList() ?: mutableListOf()
        if (currentList.size < 5 && !currentList.contains(movie)) {
            currentList.add(movie)
            _selectedSeries.value = currentList
        } else if (currentList.size >= 5) {
            _errorMessage.value = Event("You can only select up to 5 movies")
        } else {
            _errorMessage.value = Event("This movie has already been selected")
        }
    }
    fun removeSelectedSerial(position: Int) {
        val currentList = _selectedSeries.value?.toMutableList() ?: mutableListOf()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            _selectedSeries.value = currentList
        }
    }
    private fun fetchMovieGenres() {
        viewModelScope.launch {
            repository.getTvGenres()
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
}