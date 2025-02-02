package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.HomeRepository
import azari.amirhossein.filmora.models.HomePageData
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
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {

    private val _homePageData by lazy {
        MutableStateFlow<NetworkRequest<HomePageData>?>(null)
    }
    val homePageData: StateFlow<NetworkRequest<HomePageData>?> = _homePageData

    private val _randomMoviePoster = MutableStateFlow<String?>(null)
    val randomMoviePoster: StateFlow<String?> = _randomMoviePoster

    private val _randomTvPoster = MutableStateFlow<String?>(null)
    val randomTvPoster: StateFlow<String?> = _randomTvPoster

    private var cachedData: HomePageData? = null

    init {
        networkChecker.startMonitoring()
        monitorNetworkChanges()
    }

    private fun monitorNetworkChanges() {
        viewModelScope.launch {
            networkChecker.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) handleOnlineState()
                else handleOfflineState()
            }
        }
    }
    //-----Api-----
    private fun handleOnlineState() {
        repository.getRemoteData()
            .catch { error ->
                _homePageData.value = NetworkRequest.Error(error.message ?: Constants.Message.NO_INTERNET_CONNECTION)
            }
            .onEach { result ->
                updateHomePageData(result)
                result.data?.let {
                    selectRandomPosters(it)
                    filterTrendingMovies(it)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun selectRandomPosters(data: HomePageData?) {
        val baseUrl = Constants.Network.IMAGE_BASE_URL
        _randomMoviePoster.value = data?.recommendedMovies?.data?.results
            ?.randomOrNull()?.backdropPath
            ?.let { "$baseUrl${Constants.ImageSize.ORIGINAL}$it" }

        _randomTvPoster.value = data?.recommendedTvs?.data?.results
            ?.randomOrNull()?.backdropPath
            ?.let { "$baseUrl${Constants.ImageSize.ORIGINAL}$it" }
    }

    private fun filterTrendingMovies(data: HomePageData?) {
        data?.trending?.data?.results = data?.trending?.data?.results.orEmpty()
            .filter { it.mediaType == "movie" || it.mediaType == "tv" }
    }

    // Update data
    private fun updateHomePageData(newData: NetworkRequest<HomePageData>) {
        if (_homePageData.value != newData) {
            _homePageData.value = newData
        }
    }
    //-----Local-----
    private suspend fun handleOfflineState() {
        cachedData?.let {
            _homePageData.value = NetworkRequest.Success(it)
        } ?: run {
            _homePageData.value = repository.getCachedData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}
