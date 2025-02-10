package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import javax.inject.Inject

class MayLikeMoviesPagingSource @Inject constructor(
    private val remote: RemoteDataSource,
    sessionManager: SessionManager
) : BasePagingSource<ResponseMovieType>(sessionManager,MediaType.MOVIE) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<ResponseMovieType>? {
        val response = remote.discoverMovies(buildMediaParams(preferences, page))
        return if (response.isSuccessful) {
            response.body()?.results?.map { ResponseMovieType.Movies(it) }
        } else {
            null
        }
    }

    private fun buildMediaParams(preferences: TvAndMoviePreferences, page: Int) =
        mutableMapOf<String, String>().apply {
            val favoriteGenres = preferences.favoriteGenres.joinToString("|")
            val selectedGenres = preferences.selectedGenres
                .filterNot { it in preferences.dislikedGenres }
                .joinToString("|")

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

            put("page", page.toString())
        }
}