package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.MovieDetailRepository
import azari.amirhossein.filmora.models.detail.movie.ResponseMovieDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {

    private val _movieDetails = MutableStateFlow<NetworkRequest<ResponseMovieDetails>>(NetworkRequest.Loading())
    val movieDetails: StateFlow<NetworkRequest<ResponseMovieDetails>> = _movieDetails

    private var lastRequestedId: Int? = null
    private val movieDetailsCache = mutableMapOf<Int, ResponseMovieDetails>()

    init {
        networkChecker.startMonitoring()
        monitorNetworkChanges()
    }

    private fun monitorNetworkChanges() {
        viewModelScope.launch {
            networkChecker.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) handleOnlineState() else handleOfflineState()


            }
        }
    }
    private val cleanupJob = viewModelScope.launch {
        while(true) {
            cleanExpiredRecords()
            delay(30 * 60 * 1000)
        }
    }
    private suspend fun cleanExpiredRecords() {
        val oneHourAgo = System.currentTimeMillis() - Constants.Database.DETAIL_EXPIRATION_TIME // 1 hour in milliseconds
        repository.deleteExpiredDetails(oneHourAgo)
    }

    private fun handleOnlineState() {
        lastRequestedId?.let { id ->
            getMovieDetails(id)
        }
    }

    private suspend fun handleOfflineState() {
        lastRequestedId?.let { id ->
            movieDetailsCache[id]?.let { cachedDetails ->
                _movieDetails.value = NetworkRequest.Success(cachedDetails)
            } ?: run {
                _movieDetails.value = repository.getCachedData(id)
            }
        }
    }

    fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            lastRequestedId = id

            movieDetailsCache[id]?.let { cachedDetails ->
                _movieDetails.value = NetworkRequest.Success(cachedDetails)
                return@launch
            }

            if (networkChecker.isNetworkAvailable.value) {
                repository.getMovieDetails(id).collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            result.data?.let {
                                movieDetailsCache[id] = it
                                _movieDetails.value = result
                            }
                        }

                        is NetworkRequest.Error -> _movieDetails.value = result
                        else -> Unit
                    }
                }
            } else handleOfflineState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cleanupJob.cancel()
        movieDetailsCache.clear()
        networkChecker.stopMonitoring()
    }
}