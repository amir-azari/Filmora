package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.DetailsPeopleRepository
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsPeopleViewModel @Inject constructor(
    private val repository: DetailsPeopleRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {

    private val _peopleDetails = MutableStateFlow<NetworkRequest<ResponsePeopleDetails>>(NetworkRequest.Loading())
    val peopleDetails: StateFlow<NetworkRequest<ResponsePeopleDetails>> = _peopleDetails

    private var lastRequestedId: Int? = null
    private val peopleDetailsCache = mutableMapOf<Int, ResponsePeopleDetails>()

    init {
        networkChecker.startMonitoring()
        monitorNetworkChanges()
    }

    private fun monitorNetworkChanges() {
        viewModelScope.launch {
            networkChecker.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) handleOnlineState()

            }
        }
    }

    private fun handleOnlineState() {
        lastRequestedId?.let { id ->
            getPeopleDetails(id)
        }
    }

    fun getPeopleDetails(id: Int) {
        viewModelScope.launch {
            lastRequestedId = id

            peopleDetailsCache[id]?.let { cachedDetails ->
                _peopleDetails.value = NetworkRequest.Success(cachedDetails)
                return@launch
            }

            if (networkChecker.isNetworkAvailable.value) {
                repository.getPeopleDetails(id).collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            result.data?.let {
                                peopleDetailsCache[id] = it
                                _peopleDetails.value = result
                            }
                        }
                        is NetworkRequest.Error -> _peopleDetails.value = result
                        else -> Unit
                    }
                }
            }else _peopleDetails.value = NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
        }
    }

    override fun onCleared() {
        super.onCleared()
        peopleDetailsCache.clear()
        networkChecker.stopMonitoring()
    }
}