package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.TvDetailRepository
import azari.amirhossein.filmora.models.detail.ResponseTvDetails
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
class TvDetailViewModel @Inject constructor(
    private val repository: TvDetailRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {
    private val _tvDetails =
        MutableStateFlow<NetworkRequest<ResponseTvDetails>>(NetworkRequest.Loading())
    val tvDetails: StateFlow<NetworkRequest<ResponseTvDetails>> = _tvDetails

    private var lastRequestedId: Int? = null
    private val tvDetailsCache = mutableMapOf<Int, ResponseTvDetails>()

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
            getTvDetails(id)
        }
    }

    private suspend fun handleOfflineState() {
        lastRequestedId?.let { id ->
            tvDetailsCache[id]?.let { cachedDetails ->
                _tvDetails.value = NetworkRequest.Success(cachedDetails)
            } ?: run {
                _tvDetails.value = repository.getCachedData(id)
            }
        }
    }

    fun getTvDetails(id: Int) {
        viewModelScope.launch {
            lastRequestedId = id

            tvDetailsCache[id]?.let { cachedDetails ->
                _tvDetails.value = NetworkRequest.Success(cachedDetails)
                return@launch
            }

            if (networkChecker.isNetworkAvailable.value) {
                repository.getTvDetails(id).collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            result.data?.let {
                                tvDetailsCache[id] = it
                                _tvDetails.value = result
                            }
                        }

                        is NetworkRequest.Error -> _tvDetails.value = result
                        else -> Unit
                    }
                }
            } else handleOfflineState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cleanupJob.cancel()
        tvDetailsCache.clear()
        networkChecker.stopMonitoring()
    }
}