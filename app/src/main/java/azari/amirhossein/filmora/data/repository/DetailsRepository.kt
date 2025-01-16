package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.home.HomeMediaItem
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DetailsRepository @Inject constructor(private val remote: RemoteDataSource) {

    fun getMediaDetails(id: Int, type: String): Flow<NetworkRequest<HomeMediaItem>> = flow {
        emit(NetworkRequest.Loading())

        try {
            val networkRequest: NetworkRequest<HomeMediaItem> = when (type) {
                Constants.MediaType.MOVIE -> {
                    val response = remote.getMovieDetails(id)
                    val movieDetails = NetworkResponse(response).handleNetworkResponse()
                        NetworkRequest.Success(HomeMediaItem(movie = movieDetails.data))
                }

                Constants.MediaType.TV -> {
                    val response = remote.getDetailsTvSeries(id)
                    val tvDetails = NetworkResponse(response).handleNetworkResponse()
                    NetworkRequest.Success(HomeMediaItem(tv = tvDetails.data))
                }

                else -> NetworkRequest.Error(Constants.Message.INVALID_MEDIA_TYPE)
            }

            emit(networkRequest)
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))
        }
    }.flowOn(Dispatchers.IO)
}
