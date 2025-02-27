package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.AccountDataStore
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.database.entity.MovieAccountStatesEntity
import azari.amirhossein.filmora.data.database.entity.MovieDetailEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.acccount.FavoriteRequest
import azari.amirhossein.filmora.models.acccount.ResponseDefault
import azari.amirhossein.filmora.models.acccount.WatchlistRequest
import azari.amirhossein.filmora.models.detail.RateRequest
import azari.amirhossein.filmora.models.detail.ResponseAccountStates
import azari.amirhossein.filmora.models.detail.movie.ResponseMovieDetails
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
class MovieDetailRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
    private val sessionManager: SessionManager,
    private val accountDataStore: AccountDataStore
) {

    fun getMovieDetails(id: Int): Flow<NetworkRequest<ResponseMovieDetails>> = flow {
        val params = mapOf(
            "append_to_response" to "credits,similar,recommendations,videos,images,reviews"
        )
        emit(NetworkRequest.Loading())

        try {
            val response = remote.getMovieDetails(id, params)
            val networkResponse = NetworkResponse(response).handleNetworkResponse()
            networkResponse.data?.let { local.saveMovieDetail(it.toEntity()) }
            emit(networkResponse)
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)


    fun getMovieAccountStates(movieId: Int): Flow<NetworkRequest<ResponseAccountStates>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val sessionId = sessionManager.getSessionId().firstOrNull()
            if (sessionId.isNullOrEmpty()) {
                emit(NetworkRequest.Error("Session id is missing"))
            } else {
                val response = remote.getMovieAccountStates(movieId, sessionId)
                val networkResponse = NetworkResponse(response).handleNetworkResponse()
                networkResponse.data?.let { local.saveMovieAccountStates(it.toEntity()) }
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


    fun addRating(movieId: Int, rateRequest: RateRequest): Flow<NetworkRequest<ResponseDefault>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val sessionId = sessionManager.getSessionId().firstOrNull()

            if (sessionId.isNullOrEmpty()) {
                emit(NetworkRequest.Error("Missing session id"))
            } else {
                val response = remote.addRateMovie(movieId, sessionId, rateRequest)
                emit(NetworkResponse(response).handleNetworkResponse())
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)


    fun removeRating(movieId: Int): Flow<NetworkRequest<ResponseDefault>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val sessionId = sessionManager.getSessionId().firstOrNull()

            if (sessionId.isNullOrEmpty()) {
                emit(NetworkRequest.Error("Missing session id"))
            } else {
                val response = remote.deleteRating(movieId, sessionId)
                emit(NetworkResponse(response).handleNetworkResponse())
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun deleteExpiredDetails(expirationTime: Long) {
        local.deleteExpiredMovieDetailData(expirationTime)
        local.deleteExpiredMovieAccountStates(expirationTime)

    }

    private fun ResponseMovieDetails.toEntity(): MovieDetailEntity =
        MovieDetailEntity(
            id = this.id ?: 0,
            movie = this,
        )

    private fun ResponseAccountStates.toEntity(): MovieAccountStatesEntity =
        MovieAccountStatesEntity(
            id = this.id ?: 0,
            accountStates = this,
        )
    private fun MovieAccountStatesEntity.toMovieAccountStates(): ResponseAccountStates =
        this.accountStates

    private fun MovieDetailEntity.toMovieDetail(): ResponseMovieDetails =
        this.movie

    suspend fun getCachedMovieDetailsData(id :Int): NetworkRequest<ResponseMovieDetails> {
        return try {
            // Try to get cached data
            val cachedData = local.getMovieDetailById(id).firstOrNull()
            if (cachedData != null) {
                NetworkRequest.Success(cachedData.toMovieDetail())
            } else {
                NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            NetworkRequest.Error("Failed to load cached data: ${e.localizedMessage}")
        }
    }

    suspend fun getCachedMovieAccountStatesData(id :Int): NetworkRequest<ResponseAccountStates> {
        return try {
            // Try to get cached data
            val cachedData = local.getMovieAccountStates(id).firstOrNull()
            if (cachedData != null) {

                NetworkRequest.Success(cachedData.toMovieAccountStates())
            } else {

                NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            NetworkRequest.Error("Failed to load cached data: ${e.localizedMessage}")
        }
    }
}

