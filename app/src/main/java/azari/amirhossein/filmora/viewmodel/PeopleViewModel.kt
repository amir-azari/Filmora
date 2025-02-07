package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.PeopleRepository
import azari.amirhossein.filmora.models.celebtiry.PeoplePageData
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
class PeopleViewModel @Inject constructor(
    private val repository: PeopleRepository,
    private val networkChecker: NetworkChecker,
) : ViewModel() {

    private val _peoplePageData by lazy {
        MutableStateFlow<NetworkRequest<PeoplePageData>?>(null)
    }
    val peoplePageData: StateFlow<NetworkRequest<PeoplePageData>?> = _peoplePageData


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

    //-----Api-----
    private fun handleOnlineState() {
        repository.getRemoteData()
            .catch { error ->
                _peoplePageData.value =
                    NetworkRequest.Error(error.message ?: Constants.Message.NO_INTERNET_CONNECTION)
            }
            .onEach { result ->
                updatePeoplePageData(result)

            }
            .launchIn(viewModelScope)
    }

    private fun updatePeoplePageData(newData: NetworkRequest<PeoplePageData>) {
        if (_peoplePageData.value != newData) {
            _peoplePageData.value = newData
        }
    }
}