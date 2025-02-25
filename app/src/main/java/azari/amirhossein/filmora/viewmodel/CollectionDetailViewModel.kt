package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.CollectionDetailRepository
import azari.amirhossein.filmora.models.detail.movie.ResponseCollectionDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val repository: CollectionDetailRepository,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _collectionDetails = MutableStateFlow<NetworkRequest<ResponseCollectionDetails>>(NetworkRequest.Loading())
    val collectionDetails: StateFlow<NetworkRequest<ResponseCollectionDetails>> = _collectionDetails

    private var lastRequestedCollectionId: Int? = null
    private val collectionDetailsCache = mutableMapOf<Int, ResponseCollectionDetails>()

    init {
        networkChecker.startMonitoring()
        monitorNetworkChanges()
    }

    private fun monitorNetworkChanges() {
        viewModelScope.launch {
            networkChecker.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) handleOnlineState()
                else _collectionDetails.value = NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        }
    }

    fun getCollectionDetails(collectionId: Int) {
        viewModelScope.launch {
            lastRequestedCollectionId = collectionId

            collectionDetailsCache[collectionId]?.let {
                _collectionDetails.value = NetworkRequest.Success(it)
                return@launch
            }

            if (networkChecker.isNetworkAvailable.value) {
                repository.getCollectionDetail(collectionId).collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            result.data?.let { data ->
                                collectionDetailsCache[collectionId] = data
                                _collectionDetails.value = NetworkRequest.Success(data)
                            }
                        }
                        is NetworkRequest.Error -> _collectionDetails.value = result
                        is NetworkRequest.Loading -> _collectionDetails.value = result
                    }
                }
            } else {
                _collectionDetails.value = NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        }
    }

    private fun handleOnlineState() {
        lastRequestedCollectionId?.let { getCollectionDetails(it) }
    }


    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}
