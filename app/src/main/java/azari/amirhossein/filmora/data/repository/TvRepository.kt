package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.movie.MoviePageData
import azari.amirhossein.filmora.models.tv.TvPageData
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TvRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
) {
    fun getRemoteData(): Flow<NetworkRequest<TvPageData>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val remoteData = fetchRemoteData()
            emit(NetworkRequest.Success(remoteData))
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))

        }
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchRemoteData(): TvPageData {
        // Call APIs
        val trending = remote.getTrendingTv("day")
        val tvGenres = remote.getTvGenres()
        val popular = remote.getPopularTv()
        val airingToday = remote.getNowAiringToday()
        val topRated = remote.getTopRatedTv()
        val onTheAir = remote.getOnTheAir()



        return TvPageData(
            trending = NetworkResponse(trending).handleNetworkResponse(),
            tvGenres = NetworkResponse(tvGenres).handleNetworkResponse(),
            popular = NetworkResponse(popular).handleNetworkResponse(),
            airingToday = NetworkResponse(airingToday).handleNetworkResponse(),
            topRated = NetworkResponse(topRated).handleNetworkResponse(),
            onTheAir = NetworkResponse(onTheAir).handleNetworkResponse()

        )
    }

}


