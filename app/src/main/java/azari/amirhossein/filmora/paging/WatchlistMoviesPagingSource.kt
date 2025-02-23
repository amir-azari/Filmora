package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.flow.firstOrNull

class WatchlistMoviesPagingSource(
    private val sessionManager: SessionManager,
    private val remote: RemoteDataSource,
    private val accountId: Int
) : BasePagingSource<ResponseMoviesList.Result>(sessionManager, MediaType.MOVIE) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<ResponseMoviesList.Result>? {
        val sessionId = sessionManager.getSessionId().firstOrNull() ?: return null
        val response = remote.getWatchlistMovies(accountId, sessionId, page)
        val networkResponse = NetworkResponse(response).handleNetworkResponse()
        return if (networkResponse is NetworkRequest.Success) {
            networkResponse.data?.results
        } else {
            null
        }
    }
}
