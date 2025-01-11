package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.HomePageData
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val sessionManager: SessionManager,
    private val local: LocalDataSource,
) {
    //-----Api-----

    fun getRemoteData(): Flow<NetworkRequest<HomePageData>> = flow {
        emit(NetworkRequest.Loading())

        try {
            // Get user preferences
            val moviePreferencesFlow = sessionManager.getMoviePreferences().stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Lazily,
                initialValue = TvAndMoviePreferences(
                    selectedIds = emptyList(),
                    favoriteGenres = emptySet(),
                    dislikedGenres = emptySet(),
                    selectedKeywords = emptySet(),
                    selectedGenres = emptySet()
                )
            )

            val tvPreferencesFlow = sessionManager.getTvPreferences().stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Lazily,
                initialValue = TvAndMoviePreferences(
                    selectedIds = emptyList(),
                    favoriteGenres = emptySet(),
                    dislikedGenres = emptySet(),
                    selectedKeywords = emptySet(),
                    selectedGenres = emptySet()
                )
            )

            // Collect the cached preferences
            val moviePreferences = moviePreferencesFlow.value
            val tvPreferences = tvPreferencesFlow.value

            // Fetch remote data based on user preferences
            val remoteData = fetchRemoteData(moviePreferences, tvPreferences)
            // Save the fetched data locally
            local.saveHomeData(remoteData.toEntity())
            // Emit the remote data
            emit(NetworkRequest.Success(remoteData))
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))

        }
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchRemoteData(
        moviePreferences: TvAndMoviePreferences,
        tvPreferences: TvAndMoviePreferences
    ): HomePageData {
        // Call APIs
        val trending = remote.getTrending("day")
        val movieGenres = remote.getMovieGenres()
        val tvGenres = remote.getTvGenres()
        val recommendedMovies = remote.discoverMovies(buildMediaParams(moviePreferences))
        val recommendedTvs = remote.discoverTvShows(buildMediaParams(tvPreferences))

        // Combine all fetched data
        return HomePageData(
            trending = NetworkResponse(trending).handleNetworkResponse(),
            movieGenres = NetworkResponse(movieGenres).handleNetworkResponse(),
            tvGenres = NetworkResponse(tvGenres).handleNetworkResponse(),
            recommendedMovies = NetworkResponse(recommendedMovies).handleNetworkResponse(),
            recommendedTvs = NetworkResponse(recommendedTvs).handleNetworkResponse()
        )
    }

    // Build parameters for API requests
    private fun buildMediaParams(preferences: TvAndMoviePreferences) = mutableMapOf<String, String>().apply {
        val favoriteGenres = preferences.favoriteGenres.joinToString("|")
        val selectedGenres = preferences.selectedGenres
            .filterNot { it in preferences.dislikedGenres }
            .joinToString("|")

        // Combine selected and favorite genres
        val combinedGenresSet = (selectedGenres.split("|") + favoriteGenres.split("|"))
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
    //-----Local-----

    private fun HomePageData.toEntity() = HomeEntity(
        trending = trending.data!!,
        movieGenres = movieGenres.data!!,
        tvGenres = tvGenres.data!!,
        recommendedMovies = recommendedMovies.data!!,
        recommendedTvs = recommendedTvs.data!!
    )


    private fun HomeEntity.toCombinedData() = HomePageData(
        trending = NetworkRequest.Success(this.trending),
        movieGenres = NetworkRequest.Success(this.movieGenres),
        tvGenres = NetworkRequest.Success(this.tvGenres),
        recommendedMovies = NetworkRequest.Success(this.recommendedMovies),
        recommendedTvs = NetworkRequest.Success(this.recommendedTvs)
    )

    // Get cached data
    suspend fun getCachedData(): NetworkRequest<HomePageData> {
        return try {
            // Try to get cached data
            val cachedData = local.getHomeData().firstOrNull()
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
