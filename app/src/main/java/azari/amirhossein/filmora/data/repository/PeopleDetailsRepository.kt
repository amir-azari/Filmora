package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.database.entity.PeopleDetailEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PeopleDetailsRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
) {
    fun getPeopleDetails(id: Int): Flow<NetworkRequest<ResponsePeopleDetails>> = flow {
        val params = mapOf("append_to_response" to "movie_credits,tv_credits,images")
        emit(NetworkRequest.Loading())
        try {
            val response = remote.getPeopleDetails(id, params)
            val networkResponse = NetworkResponse(response).handleNetworkResponse()
            networkResponse.data?.let { local.savePeopleDetail(it.toEntity()) }
            emit(networkResponse)

        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    private fun ResponsePeopleDetails.toEntity(): PeopleDetailEntity =
        PeopleDetailEntity(
            id = this.id ?: 0,
            detail = this,
            timestamp = System.currentTimeMillis()
        )

    private fun PeopleDetailEntity.toPeopleDetail(): ResponsePeopleDetails =
        this.detail
    suspend fun deleteExpiredDetails(expirationTime: Long) {
        local.deleteExpiredMediaDetailData(expirationTime)
    }

    // Get cached data
    suspend fun getCachedData(id :Int): NetworkRequest<ResponsePeopleDetails> {
        return try {
            // Try to get cached data
            val cachedData = local.getPeopleDetailById(id).firstOrNull()
            if (cachedData != null) {
                NetworkRequest.Success(cachedData.toPeopleDetail())
            } else {
                NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            NetworkRequest.Error("Failed to load cached data: ${e.localizedMessage}")
        }
    }
}