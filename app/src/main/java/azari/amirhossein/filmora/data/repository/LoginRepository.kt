package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val remote: RemoteDataSource
) {

    fun authenticateUser(username: String, password: String): Flow<NetworkRequest<String>> = flow {
        emit(NetworkRequest.Loading())
        // Request Token
        val tokenResponse = remote.requestToken()
        val tokenNetworkResponse = NetworkResponse(tokenResponse).handleNetworkResponse()

        if (tokenNetworkResponse is NetworkRequest.Success) {
            tokenNetworkResponse.data?.requestToken?.let { requestToken ->
                // Validate with Login
                val loginRequest = RequestLogin(password, requestToken, username)
                val loginResponse = remote.validateWithLogin(loginRequest)
                val loginNetworkResponse = NetworkResponse(loginResponse).handleNetworkResponse()

                if (loginNetworkResponse is NetworkRequest.Success) {
                    // Create Session
                    val sessionRequest = RequestSession(requestToken)
                    val sessionResponse = remote.createSession(sessionRequest)
                    val sessionNetworkResponse = NetworkResponse(sessionResponse).handleNetworkResponse()

                    if (sessionNetworkResponse is NetworkRequest.Success) {
                        sessionNetworkResponse.data?.sessionId?.let { sessionId ->
                            // Save session ID
                            sessionManager.saveSessionId(sessionId, false)
                            emit(NetworkRequest.Success(sessionId))
                        } ?: emit(NetworkRequest.Error(Constants.Message.SESSION_NULL))
                    } else {
                        emit(NetworkRequest.Error((sessionNetworkResponse as NetworkRequest.Error).message!!))
                    }
                } else {
                    emit(NetworkRequest.Error((loginNetworkResponse as NetworkRequest.Error).message!!))
                }
            } ?: emit(NetworkRequest.Error(Constants.Message.TOKEN_NULL))
        } else {
            emit(NetworkRequest.Error((tokenNetworkResponse as NetworkRequest.Error).message!!))
        }
    }.flowOn(Dispatchers.IO)


    fun authenticateGuest(): Flow<NetworkRequest<String>> = flow {
        emit(NetworkRequest.Loading())

        try {
            // Create a guest session
            val response = remote.createGuestSession()
            val networkResponse = NetworkResponse(response).handleNetworkResponse()

            if (networkResponse is NetworkRequest.Success) {
                networkResponse.data?.guestSessionId?.let { guestSessionId ->
                    // Save the Guest Session ID
                    sessionManager.saveSessionId(guestSessionId, true)
                    emit(NetworkRequest.Success(guestSessionId))
                } ?: emit(NetworkRequest.Error(Constants.Message.SESSION_NULL))
            } else {
                emit(NetworkRequest.Error((networkResponse as NetworkRequest.Error).message!!))
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun checkSessionStatus(): Flow<Boolean> {
        return sessionManager.getSessionId().map { sessionId ->
            sessionId?.isNotEmpty() ?: false
        }
    }

    fun getSessionId(): Flow<String?> = sessionManager.getSessionId()
}
