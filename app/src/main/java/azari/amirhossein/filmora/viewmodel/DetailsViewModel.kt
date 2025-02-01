package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.DetailsRepository
import azari.amirhossein.filmora.models.HomePageData
import azari.amirhossein.filmora.models.detail.DetailMediaItem
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.models.detail.ResponseMovieRecommendations
import azari.amirhossein.filmora.models.detail.ResponseMovieSimilar
import azari.amirhossein.filmora.models.detail.ResponseTvRecommendations
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.models.detail.ResponseVideo
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
class DetailsViewModel @Inject constructor(
    private val repository: DetailsRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {

    private val _mediaDetails = MutableStateFlow<NetworkRequest<DetailMediaItem>>((NetworkRequest.Loading()))
    val mediaDetails: StateFlow<NetworkRequest<DetailMediaItem>> = _mediaDetails

    private val _movieSimilar = MutableStateFlow<NetworkRequest<ResponseMovieSimilar>>(NetworkRequest.Loading())
    val movieSimilar: StateFlow<NetworkRequest<ResponseMovieSimilar>> = _movieSimilar

    private val _movieRecommendations = MutableStateFlow<NetworkRequest<ResponseMovieRecommendations>>(NetworkRequest.Loading())
    val movieRecommendations: StateFlow<NetworkRequest<ResponseMovieRecommendations>> = _movieRecommendations

    private val _tvSimilar = MutableStateFlow<NetworkRequest<ResponseTvSimilar>>(NetworkRequest.Loading())
    val tvSimilar: StateFlow<NetworkRequest<ResponseTvSimilar>> = _tvSimilar

    private val _tvRecommendations = MutableStateFlow<NetworkRequest<ResponseTvRecommendations>>(NetworkRequest.Loading())
    val tvRecommendations: StateFlow<NetworkRequest<ResponseTvRecommendations>> = _tvRecommendations

    private val _videos = MutableStateFlow<NetworkRequest<ResponseVideo>>(NetworkRequest.Loading())
    val videos: StateFlow<NetworkRequest<ResponseVideo>> = _videos

    private val _images = MutableStateFlow<NetworkRequest<ResponseImage>>(NetworkRequest.Loading())
    val images: StateFlow<NetworkRequest<ResponseImage>> = _images

    private var lastRequestedMediaId: Int? = null
    private var lastRequestedMediaType: String? = null
    private val mediaDetailsCache = mutableMapOf<String, DetailMediaItem>()

    init {
        networkChecker.startMonitoring()
        monitorNetworkChanges()
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
    fun updateMediaDetails(mediaItem: DetailMediaItem) {
        mediaItem.similar?.let { _movieSimilar.value = NetworkRequest.Success(it) }
        mediaItem.recommendations?.let { _movieRecommendations.value = NetworkRequest.Success(it) }
        mediaItem.tvSimilar?.let { _tvSimilar.value = NetworkRequest.Success(it) }
        mediaItem.tvRecommendations?.let { _tvRecommendations.value = NetworkRequest.Success(it) }
        mediaItem.videos?.let { _videos.value = NetworkRequest.Success(it) }
        mediaItem.images?.let { _images.value = NetworkRequest.Success(it) }
    }


    private fun monitorNetworkChanges() {
        viewModelScope.launch {
            networkChecker.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) handleOnlineState()
                else handleOfflineState()
            }
        }
    }

    private fun handleOnlineState() {
        lastRequestedMediaId?.let { id ->
            lastRequestedMediaType?.let { type ->
                getMediaDetails(id, type)
            }
        }
    }
    private suspend fun handleOfflineState() {
        lastRequestedMediaId?.let { id ->
            val cacheKey = "${lastRequestedMediaType}_$id"
            mediaDetailsCache[cacheKey]?.let { cachedDetails ->
                _mediaDetails.value = NetworkRequest.Success(cachedDetails)
            } ?: run {
                _mediaDetails.value = repository.getCachedData(id)
            }
        }
    }

    private fun setErrorState() {
        _mediaDetails.value = NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
    }

    fun getMediaDetails(id: Int, type: String) {
        viewModelScope.launch {
            lastRequestedMediaId = id
            lastRequestedMediaType = type

            val cacheKey = "${type}_$id"
            mediaDetailsCache[cacheKey]?.let { cachedDetails ->
                _mediaDetails.value = NetworkRequest.Success(cachedDetails)
                return@launch
            }

            if (networkChecker.isNetworkAvailable.value) {
                repository.getMediaDetails(id, type).collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            result.data?.let {
                                mediaDetailsCache[cacheKey] = it
                                updateMediaDetails(it)
                            }
                            _mediaDetails.value = result
                        }
                        is NetworkRequest.Error -> _mediaDetails.value = result
                        else -> Unit
                    }
                }
            } else {
                handleOfflineState()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cleanupJob.cancel()
        mediaDetailsCache.clear()
        networkChecker.stopMonitoring()
    }
}