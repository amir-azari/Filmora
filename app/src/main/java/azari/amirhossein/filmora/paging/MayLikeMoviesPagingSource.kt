package azari.amirhossein.filmora.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
class MayLikeMoviesPagingSource @Inject constructor(
    private val remote: RemoteDataSource,
    private val sessionManager: SessionManager
) : PagingSource<Int, ResponseMoviesList.Result>() {

    override fun getRefreshKey(state: PagingState<Int, ResponseMoviesList.Result>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResponseMoviesList.Result> {
        return try {
            val page = params.key ?: 1
            val moviePreferences =
                sessionManager.getMoviePreferences().firstOrNull() ?: TvAndMoviePreferences(
                    selectedIds = emptyList(),
                    favoriteGenres = emptySet(),
                    dislikedGenres = emptySet(),
                    selectedKeywords = emptySet(),
                    selectedGenres = emptySet()
                )

            val response = remote.discoverMovies(buildMediaParams(moviePreferences, page))

            if (response.isSuccessful) {
                val data = response.body()
                val movies = data?.results ?: emptyList()
                LoadResult.Page(
                    data = movies,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page < (data?.totalPages ?: 1)) page + 1 else null
                )
            } else {
                LoadResult.Error(Exception("Oops! Somethings went wrong."))
            }
        } catch (e: Exception) {
            LoadResult.Error(Exception("Oops! Somethings went wrong."))
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
