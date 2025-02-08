package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.movie.MoviePageData
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
            local.saveMovieData(remoteData.toEntity())
            emit(NetworkRequest.Success(remoteData))
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))

        }
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchRemoteData(): MoviePageData {
        // Call APIs
        val trending = remote.getTrendingMovie("day")
        val movieGenres = remote.getMovieGenres()
        val popular = remote.getPopularMovie()
        val nowPlaying = remote.getNowPlaying()
        val topRated = remote.getTopRatedMovie()
        val upcoming = remote.getUpcoming()


        return MoviePageData(
            trending = NetworkResponse(trending).handleNetworkResponse(),
            movieGenres = NetworkResponse(movieGenres).handleNetworkResponse(),
            popular = NetworkResponse(popular).handleNetworkResponse() ,
            nowPlaying = NetworkResponse(nowPlaying).handleNetworkResponse(),
            topRated = NetworkResponse(topRated).handleNetworkResponse(),
            upcoming = NetworkResponse(upcoming).handleNetworkResponse()
            )
    }

    private fun MoviePageData.toEntity() = MovieEntity(
        trending = trending.data!!,
        movieGenres = movieGenres.data!!,
        popular = popular.data!!,
        nowPlaying = nowPlaying.data!!,
        topRated = topRated.data!!,
        upcoming = upcoming.data!!

    )


    private fun MovieEntity.toCombinedData() = MoviePageData(
        trending = NetworkRequest.Success(this.trending),
        movieGenres = NetworkRequest.Success(this.movieGenres),
        popular = NetworkRequest.Success(this.popular),
        nowPlaying = NetworkRequest.Success(this.nowPlaying),
        topRated = NetworkRequest.Success(this.topRated),
        upcoming = NetworkRequest.Success(this.upcoming)
    )

    // Get cached data
    suspend fun getCachedData(): NetworkRequest<MoviePageData> {
        return try {
            // Try to get cached data
            val cachedData = local.getMovieData().firstOrNull()
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