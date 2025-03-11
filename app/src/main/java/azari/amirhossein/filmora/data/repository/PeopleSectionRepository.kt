package azari.amirhossein.filmora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleType
import azari.amirhossein.filmora.paging.PopularPeoplePagingSource
import azari.amirhossein.filmora.paging.TrendingPeoplePagingSource
import azari.amirhossein.filmora.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeopleSectionRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val remote: RemoteDataSource
) {

    fun getPeoples(sectionType: String): Flow<PagingData<ResponsePeopleType>> {
        val pagingSource = when (sectionType) {
            Constants.SectionType.POPULAR_PEOPLE-> PopularPeoplePagingSource(remote, sessionManager)
            Constants.SectionType.TRENDING_PEOPLE -> TrendingPeoplePagingSource(remote, sessionManager)
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