package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.movie.MoviePageData
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
) {
    fun getRemoteData(): Flow<NetworkRequest<MoviePageData>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val remoteData = fetchRemoteData()
            emit(NetworkRequest.Success(remoteData))
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))

        }
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchRemoteData(
    ): MoviePageData {
        // Call APIs
        val trending = remote.getTrendingMovie("day")
        val movieGenres = remote.getMovieGenres()
        val popular = remote.getPopularMovie()


        return MoviePageData(
            trending = NetworkResponse(trending).handleNetworkResponse(),
            movieGenres = NetworkResponse(movieGenres).handleNetworkResponse(),
            popular = NetworkResponse(popular).handleNetworkResponse()
            )
    }
}