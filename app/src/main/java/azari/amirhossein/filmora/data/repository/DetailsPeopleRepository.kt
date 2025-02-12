package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DetailsPeopleRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
) {
    fun getPeopleDetails(id: Int): Flow<NetworkRequest<ResponsePeopleDetails>> = flow {
        val params = mapOf("append_to_response" to "movie_credits,tv_credits,images")
        emit(NetworkRequest.Loading())
        try {
            val response = remote.getPeopleDetails(id, params)
            val networkResponse = NetworkResponse(response).handleNetworkResponse()
            if (networkResponse is NetworkRequest.Success) {
                emit(networkResponse)
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}