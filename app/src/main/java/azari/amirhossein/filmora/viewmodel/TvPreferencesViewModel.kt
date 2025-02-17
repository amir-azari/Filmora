package azari.amirhossein.filmora.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.repository.TvPreferencesRepository
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.Event
import azari.amirhossein.filmora.utils.NetworkChecker
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
    private val networkChecker: NetworkChecker

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

    private val _savePreferencesResult = MutableLiveData<NetworkRequest<Unit>>()
    val savePreferencesResult: LiveData<NetworkRequest<Unit>> = _savePreferencesResult

    val isNetworkAvailable = networkChecker.isNetworkAvailable


    init {
        networkChecker.startMonitoring()
        setupSearchQueryFlow()
        fetchTvGenres()


    }
    private fun setupSearchQueryFlow() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .filter { query ->
                    if (query.isEmpty()) {
                        return@filter false
                    }
                    if (!isNetworkAvailable.value) {
                        _errorMessage.value = Event(Constants.Message.NO_INTERNET_CONNECTION)
                        return@filter false
                    }
                    true
                }
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
            _errorMessage.value = Event("You can only select up to 5 series")
        } else {
            _errorMessage.value = Event("This serial has already been selected")
        }
    }
    fun removeSelectedSerial(position: Int) {
        val currentList = _selectedSeries.value?.toMutableList() ?: mutableListOf()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            _selectedSeries.value = currentList
        }
    }
    private fun fetchTvGenres() {
        viewModelScope.launch {
            if (!isNetworkAvailable.value) {
                _errorMessage.value = Event(Constants.Message.NO_INTERNET_CONNECTION)
                return@launch

            }
            repository.getTvGenres().collect { result ->
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

    private suspend fun fetchTvKeywords(tvId: Int): Set<Int> {
        if (!isNetworkAvailable.value) {
            return emptySet()
        }
        var keywordIds = emptySet<Int>()
        repository.getTvKeywords(tvId).collect { result ->
            when (result) {
                is NetworkRequest.Success -> {
                    keywordIds = result.data?.results?.mapNotNull { it?.id }?.toSet() ?: emptySet()
                }
                else -> {}
            }
        }
        return keywordIds
    }
    fun savePreferences() = viewModelScope.launch {
        if (!isNetworkAvailable.value) {
            _errorMessage.value = Event(Constants.Message.NO_INTERNET_CONNECTION)
            return@launch
        }
        try {
            _savePreferencesResult.value = NetworkRequest.Loading()


            val selectedTvs = _selectedSeries.value ?: emptyList()
            val allKeywords = mutableSetOf<Int>()

            selectedTvs.forEach { tv ->
                tv.id?.let { tvId ->
                    val tvKeywords = fetchTvKeywords(tvId)
                    allKeywords.addAll(tvKeywords)
                }
            }

            val preferences = TvAndMoviePreferences(
                selectedIds = selectedTvs.mapNotNull { it.id },
                favoriteGenres = _selectedFavoriteGenres.value ?: emptySet(),
                dislikedGenres = _selectedDislikedGenres.value ?: emptySet(),
                selectedKeywords = allKeywords,
                selectedGenres = selectedTvs.flatMap { it.genreIds ?: emptyList() }.filterNotNull().toSet()
            )

            sessionManager.saveTvPreferences(preferences)
            _savePreferencesResult.value = NetworkRequest.Success(Unit)

        } catch (e: Exception) {
            _savePreferencesResult.value = NetworkRequest.Error(e.message ?: Constants.Message.UNKNOWN_ERROR)
            Log.e("Keywords", "Error saving preferences: ${e.message}")
        }
    }
    fun fetchRetry(){
        fetchTvGenres()
    }
    fun validatePreferences(): Boolean {
        val selectedTvs = _selectedSeries.value ?: emptyList()
        val favoriteGenres = _selectedFavoriteGenres.value ?: emptySet()
        val dislikedGenres = _selectedDislikedGenres.value ?: emptySet()

        if (selectedTvs.size < 5) {
            _errorMessage.value = Event("Please select exactly 5 series")
            return false
        }

        if (favoriteGenres.isEmpty() || dislikedGenres.isEmpty()) {
            _errorMessage.value = Event("Please select at least one genre preference")
            return false
        }

        return true
    }
    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}