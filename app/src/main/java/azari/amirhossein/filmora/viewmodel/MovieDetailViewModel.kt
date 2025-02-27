package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.MovieDetailRepository
import azari.amirhossein.filmora.models.acccount.FavoriteRequest
import azari.amirhossein.filmora.models.acccount.WatchlistRequest
import azari.amirhossein.filmora.models.detail.RateRequest
import azari.amirhossein.filmora.models.detail.movie.ResponseMovieDetails
import azari.amirhossein.filmora.ui.detail.AccountStatesUiState
import azari.amirhossein.filmora.ui.detail.MediaActionState
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {

    private val _movieDetails =
        MutableStateFlow<NetworkRequest<ResponseMovieDetails>>(NetworkRequest.Loading())
    val movieDetails = _movieDetails.asStateFlow()

    private val _accountStates =
        MutableStateFlow<AccountStatesUiState>(AccountStatesUiState.Loading)
    val accountStates = _accountStates.asStateFlow()

    private val _mediaActionState = MutableStateFlow(MediaActionState())
    val mediaActionState = _mediaActionState.asStateFlow()

    private var currentMovieId: Int = -1
    private val movieDetailsCache = mutableMapOf<Int, ResponseMovieDetails>()

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
        if (currentMovieId != -1) {
            getMovieDetails(currentMovieId)
            getMovieAccountStates(currentMovieId)
        }
    }

    private suspend fun handleOfflineState() {
        currentMovieId.takeIf { it != -1 }?.let { id ->
            // First try from cache
            movieDetailsCache[id]?.let { cachedDetails ->
                _movieDetails.value = NetworkRequest.Success(cachedDetails)
            } ?: run {
                // Try from local database
                when (val cachedMovieDetails = repository.getCachedMovieDetailsData(id)) {
                    is NetworkRequest.Success -> {
                        _movieDetails.value = cachedMovieDetails
                    }

                    is NetworkRequest.Error -> {
                        _movieDetails.value = cachedMovieDetails

                    }

                    else -> {
                        _movieDetails.value = NetworkRequest.Loading()
                    }
                }

                // Handle account states
                when (val cachedStates = repository.getCachedMovieAccountStatesData(id)) {
                    is NetworkRequest.Success -> {
                        _accountStates.value = cachedStates.data?.let {
                            AccountStatesUiState.Success(it)
                        } ?: AccountStatesUiState.Error("No cached account states")
                    }

                    is NetworkRequest.Error -> {
                        _accountStates.value =
                            AccountStatesUiState.Error(cachedStates.message.toString())
                    }

                    else -> {
                        _accountStates.value = AccountStatesUiState.Loading
                    }
                }
            }
        }
    }

    fun getMovieDetails(movieId: Int) {
        currentMovieId = movieId
        viewModelScope.launch {
            _movieDetails.value = NetworkRequest.Loading()

            try {
                if (networkChecker.isNetworkAvailable.value) {
                    repository.getMovieDetails(movieId).collect { result ->
                        if (result is NetworkRequest.Success) {
                            result.data?.let { movieDetailsCache[movieId] = it }
                        }
                        _movieDetails.value = result
                    }
                } else {
                    handleOfflineState()
                }
            } catch (e: Exception) {
                _movieDetails.value = NetworkRequest.Error(e.message.toString())
            }
        }
    }

    fun getMovieAccountStates(movieId: Int) {
        viewModelScope.launch {
            _accountStates.value = AccountStatesUiState.Loading

            try {
                if (networkChecker.isNetworkAvailable.value) {
                    repository.getMovieAccountStates(movieId).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> {
                                result.data?.let {
                                    _accountStates.value = AccountStatesUiState.Success(it)
                                }
                            }

                            is NetworkRequest.Error -> {
                                _accountStates.value =
                                    AccountStatesUiState.Error(result.message.toString())
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

    fun toggleFavorite(movieId: Int, newState: Boolean) {
        viewModelScope.launch {
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    _mediaActionState.update { it.copy(isFavoriteLoading = true) }
                    val request = FavoriteRequest(
                        mediaType = "movie",
                        mediaId = movieId,
                        favorite = newState
                    )
                    repository.updateFavoriteStatus(request).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> getMovieAccountStates(movieId)
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

    fun toggleWatchlist(movieId: Int, newState: Boolean) {
        viewModelScope.launch {
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    _mediaActionState.update { it.copy(isWatchlistLoading = true) }
                    val request = WatchlistRequest(
                        mediaType = "movie",
                        mediaId = movieId,
                        watchlist = newState
                    )
                    repository.updateWatchlistStatus(request).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> getMovieAccountStates(movieId)
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

    fun addRating(movieId: Int, rating: Float) {
        viewModelScope.launch {
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    _mediaActionState.update { it.copy(isRatingLoading = true) }
                    val request = RateRequest(rating.toDouble())
                    repository.addRating(movieId, request).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> {
                                delay(1500)
                                getMovieAccountStates(movieId)
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

    fun removeRating(movieId: Int) {
        viewModelScope.launch {
            try {
                if (networkChecker.isNetworkAvailable.value) {
                    _mediaActionState.update { it.copy(isRatingLoading = true) }
                    repository.removeRating(movieId).collect { result ->
                        when (result) {
                            is NetworkRequest.Success -> {
                                delay(1500)
                                getMovieAccountStates(movieId)
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
        networkChecker.stopMonitoring()
        movieDetailsCache.clear()
    }
}