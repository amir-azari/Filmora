package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.AccountDataStore
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.database.entity.TvAccountStatesEntity
import azari.amirhossein.filmora.data.database.entity.TvDetailEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.acccount.FavoriteRequest
import azari.amirhossein.filmora.models.acccount.ResponseDefault
import azari.amirhossein.filmora.models.acccount.WatchlistRequest
import azari.amirhossein.filmora.models.detail.RateRequest
import azari.amirhossein.filmora.models.detail.ResponseAccountStates
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvDetailRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
    private val sessionManager: SessionManager,
    private val accountDataStore: AccountDataStore
) {

    fun getTvDetails(id: Int): Flow<NetworkRequest<ResponseTvDetails>> = flow {
        val params = mapOf(
            "append_to_response" to "credits,similar,recommendations,videos,images,reviews"
        )
        emit(NetworkRequest.Loading())

        try {
            val response = remote.getTvDetails(id, params)
            val networkResponse = NetworkResponse(response).handleNetworkResponse()
            networkResponse.data?.let { local.saveTvDetail(it.toEntity()) }
            emit(networkResponse)
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun getTvAccountStates(tvId: Int): Flow<NetworkRequest<ResponseAccountStates>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val sessionId = sessionManager.getSessionId().firstOrNull()
            if (sessionId.isNullOrEmpty()) {
                emit(NetworkRequest.Error("Session id is missing"))
            } else {
                val response = remote.getTvAccountStates(tvId, sessionId)
                val networkResponse = NetworkResponse(response).handleNetworkResponse()
                networkResponse.data?.let { local.saveTvAccountStates(it.toEntity()) }
                emit(networkResponse)
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun updateFavoriteStatus(favoriteRequest: FavoriteRequest): Flow<NetworkRequest<ResponseDefault>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val sessionId = sessionManager.getSessionId().firstOrNull()
            val account = accountDataStore.getCachedAccountDetails().firstOrNull()

            if (sessionId.isNullOrEmpty() || account == null) {
                emit(NetworkRequest.Error("Missing session or account details"))
            } else {
                val response = remote.markAsFavorite(account.id!!, sessionId, favoriteRequest)
                emit(NetworkResponse(response).handleNetworkResponse())
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun updateWatchlistStatus(watchlistRequest: WatchlistRequest): Flow<NetworkRequest<ResponseDefault>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val sessionId = sessionManager.getSessionId().firstOrNull()
            val account = accountDataStore.getCachedAccountDetails().firstOrNull()

            if (sessionId.isNullOrEmpty() || account == null) {
                emit(NetworkRequest.Error("Missing session or account details"))
            } else {
                val response = remote.markWatchlist(account.id!!, sessionId, watchlistRequest)
                emit(NetworkResponse(response).handleNetworkResponse())
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun addRating(tvId: Int, rateRequest: RateRequest): Flow<NetworkRequest<ResponseDefault>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val sessionId = sessionManager.getSessionId().firstOrNull()

            if (sessionId.isNullOrEmpty()) {
                emit(NetworkRequest.Error("Missing session id"))
            } else {
                val response = remote.addRateTv(tvId, sessionId, rateRequest)
                emit(NetworkResponse(response).handleNetworkResponse())
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun removeRating(tvId: Int): Flow<NetworkRequest<ResponseDefault>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val sessionId = sessionManager.getSessionId().firstOrNull()

            if (sessionId.isNullOrEmpty()) {
                emit(NetworkRequest.Error("Missing session id"))
            } else {
                val response = remote.deleteTvRating(tvId, sessionId)
                emit(NetworkResponse(response).handleNetworkResponse())
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deleteExpiredDetails(expirationTime: Long) {
        local.deleteExpiredTvDetailData(expirationTime)
        local.deleteExpiredTvAccountStates(expirationTime)
    }

    private fun ResponseTvDetails.toEntity(): TvDetailEntity =
        TvDetailEntity(
            id = this.id ?: 0,
            tv = this,
            timestamp = System.currentTimeMillis()
        )

    private fun ResponseAccountStates.toEntity(): TvAccountStatesEntity =
        TvAccountStatesEntity(
            id = this.id ?: 0,
            accountStates = this,
            timestamp = System.currentTimeMillis()
        )

    private fun TvAccountStatesEntity.toTvAccountStates(): ResponseAccountStates =
        this.accountStates

    private fun TvDetailEntity.toTvDetail(): ResponseTvDetails =
        this.tv

    suspend fun getCachedTvDetailsData(id: Int): NetworkRequest<ResponseTvDetails> {
        return try {
            val cachedData = local.getTvDetailById(id).firstOrNull()
            if (cachedData != null) {
                NetworkRequest.Success(cachedData.toTvDetail())
            } else {
                NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            NetworkRequest.Error("Failed to load cached data: ${e.localizedMessage}")
        }
    }

    suspend fun getCachedTvAccountStatesData(id: Int): NetworkRequest<ResponseAccountStates> {
        return try {
            val cachedData = local.getTvAccountStates(id).firstOrNull()
            if (cachedData != null) {
                NetworkRequest.Success(cachedData.toTvAccountStates())
            } else {
                NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            NetworkRequest.Error("Failed to load cached data: ${e.localizedMessage}")
        }
    }
}