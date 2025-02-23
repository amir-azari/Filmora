package azari.amirhossein.filmora.viewmodel

import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.repository.FavoriteRepository
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.models.search.SearchResultPageData
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: FavoriteRepository,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _movies = MutableStateFlow<NetworkRequest<PagingData<ResponseMoviesList.Result>>>(NetworkRequest.Loading())
    val movies: StateFlow<NetworkRequest<PagingData<ResponseMoviesList.Result>>> = _movies

    private val _tvs = MutableStateFlow<NetworkRequest<PagingData<ResponseTvsList.Result>>>(NetworkRequest.Loading())
    val tvs: StateFlow<NetworkRequest<PagingData<ResponseTvsList.Result>>> = _tvs

    private val _currentTabPosition = MutableStateFlow(0)
    val currentTabPosition: StateFlow<Int> = _currentTabPosition

    private var cachedMovies: PagingData<ResponseMoviesList.Result>? = null
    private var cachedTvs: PagingData<ResponseTvsList.Result>? = null

    init {
        observeNetworkAndFetchData()
    }

    private fun observeNetworkAndFetchData() {
        viewModelScope.launch {
            networkChecker.startMonitoring().collect { isAvailable ->
                if (isAvailable) {
                    fetchMovies()
                    fetchTvs()
                } else {
                    handleOfflineState()
                }
            }
        }
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            _movies.value = NetworkRequest.Loading()
            repository.getFavoriteMoviesPager()
                .cachedIn(viewModelScope)
                .collectLatest { result ->
                    cachedMovies = result
                    _movies.value = NetworkRequest.Success(result)
                }
        }
    }

    private fun fetchTvs() {
        viewModelScope.launch {
            _tvs.value = NetworkRequest.Loading()
            repository.getFavoriteTVsPager()
                .cachedIn(viewModelScope)
                .collectLatest { result ->
                    cachedTvs = result
                    _tvs.value = NetworkRequest.Success(result)
                }
        }
    }

    private fun handleOfflineState() {
        _movies.value = cachedMovies?.let { NetworkRequest.Success(it) }
            ?: NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
        _tvs.value = cachedTvs?.let { NetworkRequest.Success(it) }
            ?: NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
    }

    fun updateTabPosition(position: Int) {
        _currentTabPosition.value = position
    }

    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}

