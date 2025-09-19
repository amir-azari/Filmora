package azari.amirhossein.filmora.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.models.authentication.CreateSessionRequest
import azari.amirhossein.filmora.data.models.authentication.ValidateLoginRequest
import azari.amirhossein.filmora.data.repository.UserPreferencesRepository
import azari.amirhossein.filmora.domain.repository.AuthRepository
import azari.amirhossein.filmora.utils.Event
import azari.amirhossein.filmora.utils.network.Failure
import azari.amirhossein.filmora.utils.network.Resource
import azari.amirhossein.filmora.utils.view.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    // --- Input handlers ---
    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, userMessage  = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, userMessage  = null) }
    }

    // --- Login flow ---
    fun login() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            repository.createRequestToken().collect { resource ->
                when (resource) {
                    is Resource.Loading -> { _uiState.update { it.copy(isLoading = true, currentAction = AuthAction.LOGIN) } }
                    is Resource.Error -> { updateErrorState(resource.failure) }
                    is Resource.Success -> {
                        // Continue with login validation if token is valid
                        val requestToken = resource.data.requestToken
                        if (!requestToken.isNullOrBlank()) {
                            validateLoginWithToken(requestToken)
                        } else {
                            updateErrorState(UiText.StringResource(R.string.error_failed_request_token))

                        }
                    }
                }
            }
        }
    }

    // Clear session and reset state
    suspend fun logout() {
        userPreferences.clearSession()
        _uiState.value = LoginUiState()

    }

    // Validate login credentials with request token
    private suspend fun validateLoginWithToken(token: String) {
        val currentState = _uiState.value
        val loginRequest = ValidateLoginRequest(
            username = currentState.username,
            password = currentState.password,
            requestToken = token
        )

        repository.validateLogin(loginRequest).collect { resource ->
            when (resource) {
                is Resource.Loading -> {/* No-op */ }
                is Resource.Error -> { updateErrorState(resource.failure) }
                is Resource.Success -> {
                    val validatedToken = resource.data.requestToken
                    if (!validatedToken.isNullOrBlank()) {
                        createSession(validatedToken)
                    } else {
                        updateErrorState(UiText.StringResource(R.string.error_failed_request_token))

                    }
                }
            }
        }
    }

    // Create session after successful login validation
    private suspend fun createSession(validatedToken: String) {
        val sessionRequest = CreateSessionRequest(requestToken = validatedToken)

        repository.createSession(sessionRequest).collect { resource ->
            when (resource) {
                is Resource.Loading -> {/* No-op */}
                is Resource.Error -> { updateErrorState(resource.failure) }
                is Resource.Success -> {
                    resource.data.sessionId?.let { id ->
                        if (id.isNotBlank()) {
                            userPreferences.saveSessionId(id)
                        }
                    }
                    // Update state and navigate to Preferences
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            navigationEvent = Event(AuthNavigation.ToPreferences),
                            sessionId = resource.data.sessionId
                        )
                    }
                }
            }
        }
    }

    // --- Guest login flow ---
    fun continueAsGuest() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            repository.createGuestSession().collect { resource ->
                when (resource) {
                    is Resource.Loading -> { _uiState.update { it.copy(isLoading = true, currentAction = AuthAction.CONTINUE) } }
                    is Resource.Error -> { updateErrorState(resource.failure) }
                    is Resource.Success -> {
                        // Save guest session and mark preferences as set
                        resource.data.guestSessionId?.let { id ->
                            if (id.isNotBlank()) {
                                userPreferences.saveGuestSessionId(id)
                                userPreferences.setPreferencesCompleted(true)
                            }
                        }
                        // Navigate to Home
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                navigationEvent = Event(AuthNavigation.ToHome)
                            )
                        }
                    }
                }
            }

        }
    }
    // --- Error handling ---
    private fun updateErrorState(failure: Failure) {
        val message = when (failure) {
            is Failure.NetworkConnection -> UiText.StringResource(R.string.error_no_internet_connection)
            is Failure.ServerError -> UiText.StringResource(R.string.error_server)
            is Failure.CustomError -> UiText.DynamicString(failure.message)
            is Failure.UnknownError -> UiText.StringResource(R.string.error_unknown)
            is Failure.DatabaseError -> UiText.DynamicString(failure.message)
            is Failure.EmptyResponse -> UiText.StringResource(R.string.error_empty_response)
        }
        _uiState.update { it.copy(isLoading = false, userMessage  =Event(message), currentAction = AuthAction.NONE) }
    }
    private fun updateErrorState(uiText: UiText) {
        _uiState.update {
            it.copy(isLoading = false, userMessage  = Event(uiText), currentAction = AuthAction.NONE)
        }
    }

}