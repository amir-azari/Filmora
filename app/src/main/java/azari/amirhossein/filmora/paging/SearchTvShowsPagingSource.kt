package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList

class SearchTvShowsPagingSource(
    private val remote: RemoteDataSource,
    sessionManager: SessionManager,
    private val query: String
) : BasePagingSource<ResponseTvsList.Result>(sessionManager , MediaType.MOVIE) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences):List<ResponseTvsList.Result>? {
        return try {
            val response = remote.searchTv(page, query)
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