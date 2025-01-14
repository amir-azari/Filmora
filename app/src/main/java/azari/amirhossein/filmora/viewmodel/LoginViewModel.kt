package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.LoginRepository
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.Event
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val networkChecker: NetworkChecker,

    ) : ViewModel() {

    private val _authResult = MutableStateFlow<Event<NetworkRequest<String>>?>(null)
    val authResult: StateFlow<Event<NetworkRequest<String>>?> get() = _authResult

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> get() = _isSessionActive

    init {
        networkChecker.startMonitoring()
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkChecker.isNetworkAvailable.collect { isAvailable ->
                if (!isAvailable) {
                    _authResult.value = Event(NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION))
                }
            }
        }
    }
    // Authenticate user
    fun authenticateUser(username: String, password: String) {
        viewModelScope.launch {
            if (networkChecker.isNetworkAvailable.value) {
                repository.authenticateUser(username, password)
                    .onEach { result ->
                        _authResult.value = Event(result)
                    }
                    .launchIn(viewModelScope)
            } else {
                _authResult.value =
                    Event(NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION))
            }


        }
    }

    // Authenticate guest
    fun authenticateGuest() {
        viewModelScope.launch {
            if (networkChecker.isNetworkAvailable.value) {
                repository.authenticateGuest()
                    .onEach { result ->
                        _authResult.value = Event(result)
                    }
                    .launchIn(viewModelScope)
            } else {
                _authResult.value =
                    Event(NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION))
            }

        }
    }

    fun checkSessionStatus() {
        viewModelScope.launch {
            repository.checkSessionStatus()
                .onEach { status ->
                    _isSessionActive.value = status
                }
                .launchIn(viewModelScope)
        }
    }

    suspend fun getSessionId(): String? {
        return repository.getSessionId().firstOrNull()
    }

    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}