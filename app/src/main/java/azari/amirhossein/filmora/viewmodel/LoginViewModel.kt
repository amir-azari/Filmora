package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.LoginRepository
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _authResult = MutableLiveData<NetworkRequest<String>?>()
    val authResult: MutableLiveData<NetworkRequest<String>?> get() = _authResult

    val isNetworkAvailable = networkChecker.startMonitoring()

    // Authenticate user
    fun authenticateUser(username: String, password: String) {
        viewModelScope.launch {
            repository.authenticateUser(username, password)
                .collect { result ->
                    _authResult.postValue(result)
                }
        }
    }

    fun clearAuthResult() {
        _authResult.value = null
    }

    suspend fun getSessionId(): String? {
        return repository.getSessionId().firstOrNull()
    }
}