package azari.amirhossein.filmora.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import azari.amirhossein.filmora.models.authentication.ResponseSession
import azari.amirhossein.filmora.models.authentication.ResponseToken
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepository @Inject constructor(@ApplicationContext private val context: Context, private val remote: RemoteDataSource) {

    private object StoredKey {
        val SESSION_ID = stringPreferencesKey(Constants.DataStore.SESSION_ID)
    }

    private val Context.dataStore :DataStore<Preferences> by preferencesDataStore(Constants.DataStore.PROFILE)

    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { prefs ->
            prefs[StoredKey.SESSION_ID] = sessionId
        }
    }
    fun getSessionId(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[StoredKey.SESSION_ID]
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(StoredKey.SESSION_ID)
        }
    }

    fun requestToken(): Flow<NetworkRequest<ResponseToken>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val response = remote.requestToken()
            val networkResponse = NetworkResponse(response)
            emit(networkResponse.handleNetworkResponse())
        } catch (e: Exception) {
            emit(NetworkRequest.Error("An error occurred: ${e.message}"))
        }
    }

    fun validateLogin(
        username: String,
        password: String,
        requestToken: String
    ): Flow<NetworkRequest<ResponseToken>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val body = RequestLogin(password, requestToken, username)
            Log.d("LoginRequestBody", Gson().toJson(body))

            val response = remote.validateWithLogin(body)
            val networkResponse = NetworkResponse(response)
            emit(networkResponse.handleNetworkResponse())
        } catch (e: Exception) {
            emit(NetworkRequest.Error("An error occurred: ${e.message}"))
        }
    }

    fun createSession(requestToken: String): Flow<NetworkRequest<ResponseSession>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val body = RequestSession(requestToken)
            val response = remote.createSession(body)
            val networkResponse = NetworkResponse(response)
            emit(networkResponse.handleNetworkResponse())
        } catch (e: Exception) {
            emit(NetworkRequest.Error("An error occurred: ${e.message}"))
        }
    }

}