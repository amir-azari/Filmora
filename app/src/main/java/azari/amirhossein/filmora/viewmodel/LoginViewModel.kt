package azari.amirhossein.filmora.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.LoginRepository
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import azari.amirhossein.filmora.models.authentication.ResponseSession
import azari.amirhossein.filmora.models.authentication.ResponseToken
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : ViewModel() {

    private val _requestTokenState = MutableLiveData<NetworkRequest<ResponseToken>>()
    val requestTokenState: LiveData<NetworkRequest<ResponseToken>> = _requestTokenState

    private val _validateLoginState = MutableLiveData<NetworkRequest<ResponseToken>>()
    val validateLoginState: LiveData<NetworkRequest<ResponseToken>> = _validateLoginState

    private val _createSessionState = MutableLiveData<NetworkRequest<ResponseSession>>()
    val createSessionState: LiveData<NetworkRequest<ResponseSession>> = _createSessionState

    fun requestToken() {
        viewModelScope.launch {
            repository.requestToken().collect { resource ->
                _requestTokenState.postValue(resource)
            }
        }
    }

    fun validateLogin(body: RequestLogin) {
        viewModelScope.launch {
            repository.validateLogin(body).collect { resource ->
                _validateLoginState.postValue(resource)
            }
        }
    }

    // Create Session
    fun createSession(body: RequestSession) {
        viewModelScope.launch {
            repository.createSession(body).collect { resource ->
                _createSessionState.postValue(resource)
                if (resource is NetworkRequest.Success) {
                    resource.data?.sessionId?.let {
                        saveSessionId(it)
                    }
                }
            }
        }
    }

    private fun saveSessionId(sessionId: String) {
        viewModelScope.launch {
            repository.saveSessionId(sessionId)
        }
    }

    suspend fun getSessionId(): String? {
        return repository.getSessionId().firstOrNull()
    }
}