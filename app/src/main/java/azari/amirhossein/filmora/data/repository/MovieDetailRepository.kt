package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.database.entity.MovieDetailEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.detail.ResponseMovieDetails
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
) {

    fun getMovieDetails(id: Int): Flow<NetworkRequest<ResponseMovieDetails>> = flow {
        val params = mapOf(
            "append_to_response" to "credits,similar,recommendations,videos,images,reviews,account_states"
        )
        emit(NetworkRequest.Loading())

        try {
            val response = remote.getMovieDetails(id, params)
            val networkResponse = NetworkResponse(response).handleNetworkResponse()
            networkResponse.data?.let { local.saveMovieDetail(it.toEntity()) }
            emit(networkResponse)

            emit(networkResponse)
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun deleteExpiredDetails(expirationTime: Long) {
        local.deleteExpiredMovieDetailData(expirationTime)
    }

    private fun ResponseMovieDetails.toEntity(): MovieDetailEntity =
        MovieDetailEntity(
            id = this.id ?: 0,
            movie = this,
            timestamp = System.currentTimeMillis()
        )

    private fun MovieDetailEntity.toMovieDetail(): ResponseMovieDetails =
        this.movie

    suspend fun getCachedData(id :Int): NetworkRequest<ResponseMovieDetails> {
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
}

