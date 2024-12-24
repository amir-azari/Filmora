package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.MoviePreferencesRepository
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
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _searchResult = MutableLiveData<NetworkRequest<ResponseMoviesList>>()
    val searchResult: LiveData<NetworkRequest<ResponseMoviesList>> = _searchResult

    init {
        setupSearchQueryFlow()
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


}