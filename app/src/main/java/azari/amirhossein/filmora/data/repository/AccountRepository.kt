package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.AccountDataStore
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.acccount.ResponseAccountDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val remote: RemoteDataSource,
    private val accountDataStore: AccountDataStore
) {

    fun getAccountDetails(): Flow<NetworkRequest<ResponseAccountDetails>> = flow {
        val cachedAccount = accountDataStore.getCachedAccountDetails().firstOrNull()
        if (cachedAccount != null) {
            emit(NetworkRequest.Success(cachedAccount))
        } else {
            emit(NetworkRequest.Loading())
        }

        val sessionId = sessionManager.getSessionId().firstOrNull()
        if (sessionId.isNullOrEmpty()) {
            emit(NetworkRequest.Error("Session ID not found. Please login again."))
            return@flow
        }

        try {
            val response = remote.getAccountDetails(sessionId)
            when (val networkResponse = NetworkResponse(response).handleNetworkResponse()) {
                is NetworkRequest.Success -> {
                    networkResponse.data?.let { account ->
                        accountDataStore.saveAccountDetails(account)
                        emit(NetworkRequest.Success(account))
                    } ?: emit(NetworkRequest.Error("Received empty account data."))
                }
                is NetworkRequest.Error -> {
                    emit(NetworkRequest.Error(networkResponse.message ?: Constants.Message.UNKNOWN_ERROR))
                }
                else -> {
                    emit(NetworkRequest.Error("Unexpected response state"))
                }
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message ?: Constants.Message.UNKNOWN_ERROR))
        }
    }.flowOn(Dispatchers.IO)
}
