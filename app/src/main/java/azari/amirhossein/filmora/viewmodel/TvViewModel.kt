package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.TvRepository
import azari.amirhossein.filmora.models.movie.MoviePageData
import azari.amirhossein.filmora.models.tv.TvPageData
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
class TvViewModel @Inject constructor(
    private val repository: TvRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {
    private val _tvPageData by lazy {
        MutableStateFlow<NetworkRequest<TvPageData>?>(null)
    }
    val tvPageData: StateFlow<NetworkRequest<TvPageData>?> = _tvPageData

    private val _randomMoviePoster = MutableStateFlow<String?>(null)
    val randomMoviePoster: StateFlow<String?> = _randomMoviePoster

    private var cachedData: TvPageData? = null

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
                _tvPageData.value =
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

    private fun updateHomePageData(newData: NetworkRequest<TvPageData>) {
        if (_tvPageData.value != newData) {
            _tvPageData.value = newData
        }
    }
    //-----Local-----
    private suspend fun handleOfflineState() {
        cachedData?.let {
            _tvPageData.value = NetworkRequest.Success(it)
        } ?: run {
            _tvPageData.value = repository.getCachedData()
        }
    }

    private fun selectRandomPosters(data: TvPageData?) {
        val baseUrl = Constants.Network.IMAGE_BASE_URL
        _randomMoviePoster.value = data?.trending?.data?.results
            ?.randomOrNull()?.backdropPath
            ?.let { "$baseUrl${Constants.ImageSize.ORIGINAL}$it" }

    }

}