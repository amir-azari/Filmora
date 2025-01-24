package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.DetailsRepository
import azari.amirhossein.filmora.models.detail.DetailMediaItem
import azari.amirhossein.filmora.models.detail.ResponseMovieRecommendations
import azari.amirhossein.filmora.models.detail.ResponseMovieSimilar
import azari.amirhossein.filmora.models.detail.ResponseTvRecommendations
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.Event
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: DetailsRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {

    private val _mediaDetails = MutableStateFlow<Event<NetworkRequest<DetailMediaItem>>>(Event(NetworkRequest.Loading()))
    val mediaDetails: StateFlow<Event<NetworkRequest<DetailMediaItem>>?> = _mediaDetails

    private val _movieSimilar = MutableStateFlow<NetworkRequest<ResponseMovieSimilar>>(NetworkRequest.Loading())
    val movieSimilar: StateFlow<NetworkRequest<ResponseMovieSimilar>> = _movieSimilar

    private val _movieRecommendations = MutableStateFlow<NetworkRequest<ResponseMovieRecommendations>>(NetworkRequest.Loading())
    val movieRecommendations: StateFlow<NetworkRequest<ResponseMovieRecommendations>> = _movieRecommendations

    private val _tvSimilar = MutableStateFlow<NetworkRequest<ResponseTvSimilar>>(NetworkRequest.Loading())
    val tvSimilar: StateFlow<NetworkRequest<ResponseTvSimilar>> = _tvSimilar

    private val _tvRecommendations = MutableStateFlow<NetworkRequest<ResponseTvRecommendations>>(NetworkRequest.Loading())
    val tvRecommendations: StateFlow<NetworkRequest<ResponseTvRecommendations>> = _tvRecommendations


    private var lastRequestedMediaId: Int? = null
    private var lastRequestedMediaType: String? = null

    init {
        networkChecker.startMonitoring()
        monitorNetworkChanges()
    }


    fun updateMediaDetails(mediaItem: DetailMediaItem) {
        mediaItem.similar?.let { _movieSimilar.value = NetworkRequest.Success(it) }
        mediaItem.recommendations?.let { _movieRecommendations.value = NetworkRequest.Success(it) }
        mediaItem.tvSimilar?.let { _tvSimilar.value = NetworkRequest.Success(it) }
        mediaItem.tvRecommendations?.let { _tvRecommendations.value = NetworkRequest.Success(it) }
    }


    private fun monitorNetworkChanges() {
        viewModelScope.launch {
            networkChecker.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) {
                    lastRequestedMediaId?.let { id ->
                        lastRequestedMediaType?.let { type ->
                            getMediaDetails(id, type)
                        }
                    }
                } else {
                    _mediaDetails.value =
                        Event(NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION))
                }
            }
        }
    }

    fun getMediaDetails(id: Int, type: String) {
        viewModelScope.launch {
            lastRequestedMediaId = id
            lastRequestedMediaType = type

            if (networkChecker.isNetworkAvailable.value) {
                repository.getMediaDetails(id, type)
                    .collect { result ->
                        _mediaDetails.value = Event(result)
                    }
            } else {
                _mediaDetails.value =
                    Event(NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}