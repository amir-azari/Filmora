package azari.amirhossein.filmora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import azari.amirhossein.filmora.data.AccountDataStore
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.paging.FavoriteMoviesPagingSource
import azari.amirhossein.filmora.paging.FavoriteTVPagingSource
import azari.amirhossein.filmora.paging.WatchlistMoviesPagingSource
import azari.amirhossein.filmora.paging.WatchlistTVsPagingSource
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val accountDataStore: AccountDataStore,
    private val remote: RemoteDataSource
) {

    fun getWatchlistMoviesPager(): Flow<PagingData<ResponseMoviesList.Result>> = flow {
        val sessionId = sessionManager.getSessionId().firstOrNull()
        val accountId = accountDataStore.getCachedAccountDetails().firstOrNull()?.id
        if (sessionId.isNullOrEmpty() || accountId == null) {
            throw Exception("Session or Account ID not found")
        }
        emitAll(
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {
                    WatchlistMoviesPagingSource(sessionManager, remote, accountId)
                }
            ).flow
        )
    }.flowOn(Dispatchers.IO)

    fun getWatchlistTVsPager(): Flow<PagingData<ResponseTvsList.Result>> = flow {
        val sessionId = sessionManager.getSessionId().firstOrNull()
        val accountId = accountDataStore.getCachedAccountDetails().firstOrNull()?.id
        if (sessionId.isNullOrEmpty() || accountId == null) {
            throw Exception("Session or Account ID not found")
        }
        emitAll(
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {
                    WatchlistTVsPagingSource(sessionManager, remote, accountId)
                }
            ).flow
        )
    }.flowOn(Dispatchers.IO)
}

