package azari.amirhossein.filmora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.models.tv.ResponseTvType
import azari.amirhossein.filmora.paging.MayLikeMoviesPagingSource
import azari.amirhossein.filmora.paging.MayLikeTvsPagingSource
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MayLikeTvsRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val remote: RemoteDataSource
) {
    fun getTvs(): Flow<PagingData<ResponseTvType>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { MayLikeTvsPagingSource(remote, sessionManager) }
        ).flow
            .flowOn(Dispatchers.IO)
    }
}

