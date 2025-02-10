package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import azari.amirhossein.filmora.data.repository.MayLikeTvsRepository
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.models.tv.ResponseTvType
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
class MayLikeTvsViewModel @Inject constructor(
    private val repository: MayLikeTvsRepository,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _tvs = MutableStateFlow<NetworkRequest<PagingData<ResponseTvType>>>(NetworkRequest.Loading())
    val tvs: StateFlow<NetworkRequest<PagingData<ResponseTvType>>> = _tvs

    private var cachedTvs: PagingData<ResponseTvType>? = null

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
            _tvs.value = NetworkRequest.Loading()
            repository.getTvs()
                .cachedIn(viewModelScope) // Cache the flow
                .collectLatest { result ->
                    cachedTvs = result
                    _tvs.value = NetworkRequest.Success(result)
                }
        }
    }

    private fun handleOfflineState() {
        if (cachedTvs != null) {
            _tvs.value = NetworkRequest.Success(cachedTvs!!)
        } else {
            _tvs.value = NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
        }
    }
    override fun onCleared() {
        super.onCleared()
        cachedTvs = null
        networkChecker.stopMonitoring()
    }
}

