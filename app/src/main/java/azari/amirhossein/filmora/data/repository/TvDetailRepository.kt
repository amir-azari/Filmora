package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.database.entity.TvDetailEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
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
) {
    fun getTvDetails(id: Int): Flow<NetworkRequest<ResponseTvDetails>> = flow {
        val params =
            mapOf("append_to_response" to "credits,similar,recommendations,videos,images,reviews,account_states")
        emit(NetworkRequest.Loading())
        try {
            val response = remote.getTvDetails(id, params)
            val networkResponse = NetworkResponse(response).handleNetworkResponse()
            networkResponse.data?.let { local.saveTvDetail(it.toEntity()) }
            emit(networkResponse)
        }catch (e: Exception) {
            emit(NetworkRequest.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deleteExpiredDetails(expirationTime: Long) {
        local.deleteExpiredTvDetailData(expirationTime)
    }

    private fun ResponseTvDetails.toEntity(): TvDetailEntity =
        TvDetailEntity(
            id = this.id ?: 0,
            tv = this,
            timestamp = System.currentTimeMillis()
        )

    private fun TvDetailEntity.toTvDetail(): ResponseTvDetails =
        this.tv

    suspend fun getCachedData(id :Int): NetworkRequest<ResponseTvDetails> {
        return try {
            // Try to get cached data
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
}