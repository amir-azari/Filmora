package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.MovieRepository
import azari.amirhossein.filmora.models.HomePageData
import azari.amirhossein.filmora.models.movie.MoviePageData
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {
    private val _moviePageData by lazy {
        MutableStateFlow<NetworkRequest<MoviePageData>?>(null)
    }
    val moviePageData: StateFlow<NetworkRequest<MoviePageData>?> = _moviePageData

    private val _randomMoviePoster = MutableStateFlow<String?>(null)
    val randomMoviePoster: StateFlow<String?> = _randomMoviePoster

    private var cachedData: MoviePageData? = null

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

    //-----Api-----
    private fun handleOnlineState() {
        repository.getRemoteData()
            .catch { error ->
                _moviePageData.value =
                    NetworkRequest.Error(error.message ?: Constants.Message.NO_INTERNET_CONNECTION)
            }
            .onEach { result ->
                updateHomePageData(result)
                result.data?.let {
                    selectRandomPosters(it)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateHomePageData(newData: NetworkRequest<MoviePageData>) {
        if (_moviePageData.value != newData) {
            _moviePageData.value = newData
        }
    }

    private fun selectRandomPosters(data: MoviePageData?) {
        val baseUrl = Constants.Network.IMAGE_BASE_URL
        _randomMoviePoster.value = data?.trending?.data?.results
            ?.randomOrNull()?.backdropPath
            ?.let { "$baseUrl${Constants.ImageSize.ORIGINAL}$it" }

    }

    //-----Local-----
    private suspend fun handleOfflineState() {
        cachedData?.let {
            _moviePageData.value = NetworkRequest.Success(it)
        } ?: run {
            _moviePageData.value = repository.getCachedData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}