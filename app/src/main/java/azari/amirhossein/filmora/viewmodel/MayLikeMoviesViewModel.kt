package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import azari.amirhossein.filmora.data.repository.MayLikeMoviesRepository
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MayLikeMoviesViewModel @Inject constructor(
    private val repository: MayLikeMoviesRepository,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _movies = MutableStateFlow<NetworkRequest<PagingData<ResponseMovieType>>>(NetworkRequest.Loading())
    val movies: StateFlow<NetworkRequest<PagingData<ResponseMovieType>>> = _movies

    private var cachedMovies: PagingData<ResponseMovieType>? = null

    init {
        observeNetworkAndFetchMovies()
    }

    private fun observeNetworkAndFetchMovies() {
        viewModelScope.launch {
            networkChecker.startMonitoring().collect { isAvailable ->
                if (isAvailable) {
                    fetchMovies()
                } else {
                    handleOfflineState()
                }
            }
        }
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            _movies.value = NetworkRequest.Loading()
            repository.getMovies()
                .cachedIn(viewModelScope)
                .collectLatest { result ->
                    cachedMovies = result
                    _movies.value = NetworkRequest.Success(result)
                }
        }
    }

    private fun handleOfflineState() {
        if (cachedMovies != null) {
            _movies.value = NetworkRequest.Success(cachedMovies!!)
        } else {
            _movies.value = NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
        }
    }

    override fun onCleared() {
        super.onCleared()
        cachedMovies = null
        networkChecker.stopMonitoring()
    }
}


