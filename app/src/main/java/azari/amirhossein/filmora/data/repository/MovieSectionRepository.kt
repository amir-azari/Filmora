package azari.amirhossein.filmora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.paging.*
import azari.amirhossein.filmora.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieSectionRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val remote: RemoteDataSource
) {

    fun getMovies(sectionType: String): Flow<PagingData<ResponseMovieType>> {
        val pagingSource = when (sectionType) {
            Constants.SectionType.POPULAR_MOVIE -> PopularMoviesPagingSource(remote, sessionManager)
            Constants.SectionType.NOW_PLAYING -> NowPlayingPagingSource(remote, sessionManager)
            Constants.SectionType.TOP_RATED_MOVIE -> TopRatedMoviesPagingSource(remote, sessionManager)
            Constants.SectionType.UPCOMING -> UpcomingPagingSource(remote, sessionManager)
            Constants.SectionType.TRENDING_MOVIE -> TrendingMoviesPagingSource(remote, sessionManager)
            else -> throw IllegalArgumentException("Unknown section type")
        }
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { pagingSource }
        ).flow
            .flowOn(Dispatchers.IO)
    }

}