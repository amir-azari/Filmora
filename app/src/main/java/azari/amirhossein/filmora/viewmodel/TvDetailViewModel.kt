package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.TvDetailRepository
import azari.amirhossein.filmora.models.acccount.FavoriteRequest
import azari.amirhossein.filmora.models.acccount.WatchlistRequest
import azari.amirhossein.filmora.models.detail.RateRequest
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails
import azari.amirhossein.filmora.ui.detail.AccountStatesUiState
import azari.amirhossein.filmora.ui.detail.MediaActionState
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvDetailViewModel @Inject constructor(
    private val repository: TvDetailRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {

    private val _tvDetails = MutableStateFlow<NetworkRequest<ResponseTvDetails>>(NetworkRequest.Loading())
    val tvDetails = _tvDetails.asStateFlow()

    private val _accountStates = MutableStateFlow<AccountStatesUiState>(AccountStatesUiState.Loading)
    val accountStates = _accountStates.asStateFlow()

    private val _mediaActionState = MutableStateFlow(MediaActionState())
    val mediaActionState = _mediaActionState.asStateFlow()

    private var currentTvId: Int = -1
    private val tvDetailsCache = mutableMapOf<Int, ResponseTvDetails>()

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
        val oneHourAgo = System.currentTimeMillis() - Constants.Database.DETAIL_EXPIRATION_TIME
        repository.deleteExpiredDetails(oneHourAgo)
    }

    private fun monitorNetworkChanges() {
        viewModelScope.launch {
            networkChecker.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) {
                    refreshCurrentData()
                } else {
                    handleOfflineState()
                }
            }
        }
    }

    private fun refreshCurrentData() {
        if (currentTvId != -1) {
            getTvDetails(currentTvId)
            getTvAccountStates(currentTvId)
        }
    }

    private suspend fun handleOfflineState() {
        currentTvId.takeIf { it != -1 }?.let { id ->
            tvDetailsCache[id]?.let { cachedDetails ->
                _tvDetails.value = NetworkRequest.Success(cachedDetails)
            } ?: run {
                when (val cachedTvDetails = repository.getCachedTvDetailsData(id)) {
                    is NetworkRequest.Success -> {
                        _tvDetails.value = cachedTvDetails
                    }
                    is NetworkRequest.Error -> {
                        _tvDetails.value = cachedTvDetails
                    }
                    else -> {
                        _tvDetails.value = NetworkRequest.Loading()
                    }
                }

                when (val cachedStates = repository.getCachedTvAccountStatesData(id)) {
                    is NetworkRequest.Success -> {
                        _accountStates.value = cachedStates.data?.let {
                            AccountStatesUiState.Success(it)
                        } ?: AccountStatesUiState.Error("No cached account states")
                    }
                    is NetworkRequest.Error -> {
                        _accountStates.value = AccountStatesUiState.Error(cachedStates.message.toString())
                    }
                    else -> {
                        _accountStates.value = AccountStatesUiState.Loading
                    }
                }
            }
        }
    }

    fun getTvDetails(tvId: Int) {
        currentTvId = tvId
        viewModelScope.launch {
            _tvDetails.value = NetworkRequest.Loading()


            tvDetailsCache[tvId]?.let {
                _tvDetails.value = NetworkRequest.Success(it)
                return@launch
            }
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    repository.getTvDetails(tvId).collect { result ->
                        if (result is NetworkRequest.Success) {
                            result.data?.let { tvDetailsCache[tvId] = it }
                        }
                        _tvDetails.value = result
                    }
                } else {
                    handleOfflineState()
                }
            } catch (e: Exception) {
                _tvDetails.value = NetworkRequest.Error(e.message.toString())
            }
        }
    }

    fun getTvAccountStates(tvId: Int) {
        viewModelScope.launch {
            _accountStates.value = AccountStatesUiState.Loading

            try {
                if (networkChecker.isNetworkAvailable.value) {
                    repository.getTvAccountStates(tvId).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> {
                                result.data?.let {
                                    _accountStates.value = AccountStatesUiState.Success(it)
                                }
                            }
                            is NetworkRequest.Error -> {
                                _accountStates.value = AccountStatesUiState.Error(result.message.toString())
                            }
                            is NetworkRequest.Loading -> {
                                _accountStates.value = AccountStatesUiState.Loading
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _accountStates.value = AccountStatesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite(tvId: Int, newState: Boolean) {
        viewModelScope.launch {
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    _mediaActionState.update { it.copy(isFavoriteLoading = true) }
                    val request = FavoriteRequest(
                        mediaType = "tv",
                        mediaId = tvId,
                        favorite = newState
                    )
                    repository.updateFavoriteStatus(request).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> getTvAccountStates(tvId)
                            is NetworkRequest.Error -> {
                                _accountStates.value = AccountStatesUiState.Error(result.message.toString())
                            }
                            else -> {}
                        }
                    }
                } else {
                    _accountStates.value = AccountStatesUiState.Error(Constants.Message.NO_INTERNET_CONNECTION)
                }
            } finally {
                _mediaActionState.update { it.copy(isFavoriteLoading = false) }
            }
        }
    }

    fun toggleWatchlist(tvId: Int, newState: Boolean) {
        viewModelScope.launch {
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    _mediaActionState.update { it.copy(isWatchlistLoading = true) }
                    val request = WatchlistRequest(
                        mediaType = "tv",
                        mediaId = tvId,
                        watchlist = newState
                    )
                    repository.updateWatchlistStatus(request).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> getTvAccountStates(tvId)
                            is NetworkRequest.Error -> {
                                _accountStates.value = AccountStatesUiState.Error(result.message.toString())
                            }
                            else -> {}
                        }
                    }
                } else {
                    _accountStates.value = AccountStatesUiState.Error(Constants.Message.NO_INTERNET_CONNECTION)
                }
            } finally {
                _mediaActionState.update { it.copy(isWatchlistLoading = false) }
            }
        }
    }

    fun addRating(tvId: Int, rating: Float) {
        viewModelScope.launch {
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    _mediaActionState.update { it.copy(isRatingLoading = true) }
                    val request = RateRequest(rating.toDouble())
                    repository.addRating(tvId, request).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> {
                                delay(1500)
                                getTvAccountStates(tvId)
                            }
                            is NetworkRequest.Error -> {
                                _accountStates.value = AccountStatesUiState.Error(result.message.toString())
                            }
                            else -> {}
                        }
                    }
                } else {
                    _accountStates.value = AccountStatesUiState.Error(Constants.Message.NO_INTERNET_CONNECTION)
                }
            } finally {
                _mediaActionState.update { it.copy(isRatingLoading = false) }
            }
        }
    }

    fun removeRating(tvId: Int) {
        viewModelScope.launch {
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    _mediaActionState.update { it.copy(isRatingLoading = true) }
                    repository.removeRating(tvId).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> {
                                delay(1500)
                                getTvAccountStates(tvId)
                            }
                            is NetworkRequest.Error -> {
                                _accountStates.value = AccountStatesUiState.Error(result.message.toString())
                            }
                            else -> {}
                        }
                    }
                } else {
                    _accountStates.value = AccountStatesUiState.Error(Constants.Message.NO_INTERNET_CONNECTION)
                }
            } finally {
                _mediaActionState.update { it.copy(isRatingLoading = false) }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        cleanupJob.cancel()
        tvDetailsCache.clear()
        networkChecker.stopMonitoring()
    }
}