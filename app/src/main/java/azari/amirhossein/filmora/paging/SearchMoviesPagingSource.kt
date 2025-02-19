package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList

class SearchMoviesPagingSource(
    private val remote: RemoteDataSource,
    sessionManager: SessionManager,
    private val query: String
) : BasePagingSource<ResponseMoviesList.Result>(sessionManager, MediaType.MOVIE) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<ResponseMoviesList.Result>? {
        return try {
            val response = remote.searchMovie(page, query)
            if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
