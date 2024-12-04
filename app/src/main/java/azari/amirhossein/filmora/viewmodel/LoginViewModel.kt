package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.LoginRepository
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : ViewModel() {

    private val _authResult = MutableLiveData<NetworkRequest<String>>()
    val authResult: LiveData<NetworkRequest<String>> get() = _authResult

    // Authenticate user
    fun authenticateUser(username: String, password: String) {
        viewModelScope.launch {
            repository.authenticateUser(username, password)
                .collect { result ->
                    _authResult.postValue(result)
                }
        }
    }

    suspend fun getSessionId(): String? {
        return repository.getSessionId().firstOrNull()
    }
}