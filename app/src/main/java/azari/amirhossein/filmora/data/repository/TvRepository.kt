package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.TvEntity
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
            local.saveTvData(remoteData.toEntity())
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
    private fun TvPageData.toEntity() = TvEntity(
        trending = trending.data!!,
        tvGenres = tvGenres.data!!,
        popular = popular.data!!,
        airingToday = airingToday.data!!,
        topRated = topRated.data!!,
        onTheAir = onTheAir.data!!


    )


    private fun TvEntity.toCombinedData() = TvPageData(
        trending = NetworkRequest.Success(this.trending),
        tvGenres = NetworkRequest.Success(this.tvGenres),
        popular = NetworkRequest.Success(this.popular),
        airingToday = NetworkRequest.Success(this.airingToday),
        topRated = NetworkRequest.Success(this.topRated),
        onTheAir = NetworkRequest.Success(this.onTheAir)
    )

    // Get cached data
    suspend fun getCachedData(): NetworkRequest<TvPageData> {
        return try {
            // Try to get cached data
            val cachedData = local.getTvData().firstOrNull()
            if (cachedData != null) {
                NetworkRequest.Success(cachedData.toCombinedData())
            } else {
                NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            NetworkRequest.Error("Failed to load cached data: ${e.localizedMessage}")
        }
    }

}


