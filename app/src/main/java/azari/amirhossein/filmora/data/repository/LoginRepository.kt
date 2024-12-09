package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
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

        try {
            // Request Token
            val tokenResponse = remote.requestToken()
            val tokenNetworkResponse = NetworkResponse(tokenResponse).handleNetworkResponse()
            if (tokenNetworkResponse is NetworkRequest.Success) {
                val requestToken = tokenNetworkResponse.data?.requestToken ?: throw Exception("Token is null")
                emit(NetworkRequest.Success(requestToken))

                // Validate with Login
                val loginRequest = RequestLogin(password, requestToken, username)
                val loginResponse = remote.validateWithLogin(loginRequest)
                val loginNetworkResponse = NetworkResponse(loginResponse).handleNetworkResponse()
                if (loginNetworkResponse is NetworkRequest.Success) {
                    emit(NetworkRequest.Success(requestToken))  // Pass the token to next step
                } else {
                    throw Exception((loginNetworkResponse as NetworkRequest.Error).message)
                }

                // Create Session
                val sessionRequest = RequestSession(requestToken)
                val sessionResponse = remote.createSession(sessionRequest)
                val sessionNetworkResponse = NetworkResponse(sessionResponse).handleNetworkResponse()
                if (sessionNetworkResponse is NetworkRequest.Success) {
                    val sessionId = sessionNetworkResponse.data?.sessionId ?: throw Exception("Session ID is null")
                    // Save session ID
                    sessionManager.saveSessionId(sessionId,false)
                    emit(NetworkRequest.Success(sessionId))
                } else {
                    throw Exception((sessionNetworkResponse as NetworkRequest.Error).message)
                }
            } else {
                throw Exception((tokenNetworkResponse as NetworkRequest.Error).message)
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    fun authenticateGuest(): Flow<NetworkRequest<String>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val response = remote.createGuestSession()
            val networkResponse = NetworkResponse(response).handleNetworkResponse()

            if (networkResponse is NetworkRequest.Success) {
                val guestSessionId = networkResponse.data?.guestSessionId ?: throw Exception("Guest Session ID is null")
                sessionManager.saveSessionId(guestSessionId , true)
                emit(NetworkRequest.Success(guestSessionId))
            } else {
                throw Exception((networkResponse as NetworkRequest.Error).message)
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun checkSessionStatus(): Flow<Boolean> {
        return sessionManager.getSessionId().map { sessionId ->
            sessionId?.isNotEmpty() == true
        }
    }
    fun getSessionId(): Flow<String?> = sessionManager.getSessionId()

}