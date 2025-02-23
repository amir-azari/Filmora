package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.flow.firstOrNull

class RatedTVsPagingSource(
    private val sessionManager: SessionManager,
    private val remote: RemoteDataSource,
    private val accountId: Int
) : BasePagingSource<ResponseTvsList.Result>(sessionManager, MediaType.TV) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<ResponseTvsList.Result>? {
        val sessionId = sessionManager.getSessionId().firstOrNull() ?: return null
        val response = remote.getRatedTvs(accountId, sessionId, page)
        val networkResponse = NetworkResponse(response).handleNetworkResponse()
        return if (networkResponse is NetworkRequest.Success) {
            networkResponse.data?.results
        } else {
            null
        }
    }
}
