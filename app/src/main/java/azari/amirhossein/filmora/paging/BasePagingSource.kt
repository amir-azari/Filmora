package azari.amirhossein.filmora.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import kotlinx.coroutines.flow.firstOrNull

abstract class BasePagingSource<T : Any>(
    private val sessionManager: SessionManager
) : PagingSource<Int, T>() {

    abstract suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<T>?

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 1
            val preferences = sessionManager.getTvPreferences().firstOrNull() ?: TvAndMoviePreferences(
                selectedIds = emptyList(),
                favoriteGenres = emptySet(),
                dislikedGenres = emptySet(),
                selectedKeywords = emptySet(),
                selectedGenres = emptySet()
            )

            val data = fetchData(page, preferences) ?: emptyList()
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}