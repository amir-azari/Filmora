package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.detail.ResponseCollectionDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CollectionDetailRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
) {

    fun getCollectionDetail(collectionId: Int): Flow<NetworkRequest<ResponseCollectionDetails>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val response = remote.getCollectionDetail(collectionId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkRequest.Success(it))
                } ?: emit(NetworkRequest.Error("Response body is null."))
            } else {
                emit(NetworkRequest.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))
        }
    }
}