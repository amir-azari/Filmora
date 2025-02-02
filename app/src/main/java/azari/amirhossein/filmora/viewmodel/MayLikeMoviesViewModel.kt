package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import azari.amirhossein.filmora.data.repository.MayLikeMoviesRepository
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.ui.home.HomeFragmentDirections
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

    private val _movies = MutableStateFlow<NetworkRequest<PagingData<ResponseMoviesList.Result>>>(NetworkRequest.Loading())
    val movies: StateFlow<NetworkRequest<PagingData<ResponseMoviesList.Result>>> = _movies

    init {
        observeNetworkAndFetchMovies()
    }

    private fun observeNetworkAndFetchMovies() {
        viewModelScope.launch {
            networkChecker.startMonitoring().collect { isAvailable ->
                if (isAvailable) {
                    fetchMovies()
                } else {
                    _movies.value = NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
                }
            }
        }
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            repository.getMovies()
                .cachedIn(viewModelScope) // Cache the flow
                .collectLatest { result ->
                    _movies.value = NetworkRequest.Success(result)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}

