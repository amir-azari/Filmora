package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences

class SearchCelebritiesPagingSource(
    private val remote: RemoteDataSource,
    sessionManager: SessionManager,
    private val query: String
) : BasePagingSource<ResponsePopularCelebrity.Result>(sessionManager ,MediaType.PEOPLE) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<ResponsePopularCelebrity.Result>? {
        return try {
            val response = remote.searchPeople(page, query)
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