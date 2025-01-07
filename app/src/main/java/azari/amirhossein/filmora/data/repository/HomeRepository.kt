package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.CombinedData
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val sessionManager: SessionManager,
) {
    fun getCombinedData(): Flow<NetworkRequest<CombinedData>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val moviePreferences = sessionManager.getMoviePreferences().first()
            val tvPreferences = sessionManager.getTvPreferences().first()

            val trendingMoviesFlow = flow { emit(remote.getTrending("day")) }
            val movieGenresFlow = flow { emit(remote.getMovieGenres()) }
            val tvGenresFlow = flow { emit(remote.getTvGenres()) }
            val recommendedMoviesFlow = flow { emit(remote.discoverMovies(buildMediaParams(moviePreferences))) }
            val recommendedTvsFlow = flow { emit(remote.discoverTvShows(buildMediaParams(tvPreferences))) }

            combine(
                trendingMoviesFlow,
                movieGenresFlow,
                tvGenresFlow,
                recommendedMoviesFlow,
                recommendedTvsFlow
            ) { trending, movieGenres, tvGenres, recommendedMovies, recommendedTvs ->
                CombinedData(
                    trendingMovies = NetworkResponse(trending).handleNetworkResponse(),
                    movieGenres = NetworkResponse(movieGenres).handleNetworkResponse(),
                    tvGenres = NetworkResponse(tvGenres).handleNetworkResponse(),
                    recommendedMovies = NetworkResponse(recommendedMovies).handleNetworkResponse(),
                    recommendedTvs = NetworkResponse(recommendedTvs).handleNetworkResponse()
                )
            }.collect { combinedData ->
                emit(NetworkRequest.Success(combinedData))
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))
        }
    }.flowOn(Dispatchers.IO)

    private fun buildMediaParams(preferences: TvAndMoviePreferences) = mutableMapOf<String, String>().apply {
        val favoriteGenres = preferences.favoriteGenres.joinToString("|")
        val selectedGenres = preferences.selectedGenres
            .filterNot { it in preferences.dislikedGenres }
            .joinToString("|")

        val combinedGenresSet = (favoriteGenres.split("|") + selectedGenres.split("|"))
            .toSet()
            .joinToString("|")

        if (combinedGenresSet.isNotEmpty()) {
            put(Constants.Discover.WITH_GENRES, combinedGenresSet)
        }

        val dislikedGenres = preferences.dislikedGenres.joinToString("|")
        if (dislikedGenres.isNotEmpty()) {
            put(Constants.Discover.WITHOUT_GENRES, dislikedGenres)
        }

        val selectedKeywords = preferences.selectedKeywords.joinToString("|")
        if (selectedKeywords.isNotEmpty()) {
            put(Constants.Discover.WITH_KEYWORDS, selectedKeywords)
        }
    }
}
