package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.PeopleEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.celebtiry.PeoplePageData
import azari.amirhossein.filmora.models.movie.MoviePageData
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeopleRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
) {
    fun getRemoteData(): Flow<NetworkRequest<PeoplePageData>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val remoteData = fetchRemoteData()
            local.savePeopleData(remoteData.toEntity())

            emit(NetworkRequest.Success(remoteData))
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))

        }
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchRemoteData(): PeoplePageData {
        // Call APIs
        val popular = remote.getPopularPeoples(1)
        val trending = remote.getTrendingPeoples("day",1)



        return PeoplePageData(
            popular = NetworkResponse(popular).handleNetworkResponse(),
            trending = NetworkResponse(trending).handleNetworkResponse()
        )
    }

    private fun PeoplePageData.toEntity() = PeopleEntity(
        popular = popular.data!!,
        trending = trending.data!!
    )


    private fun PeopleEntity.toCombinedData() = PeoplePageData(
        popular = NetworkRequest.Success(this.popular),
        trending = NetworkRequest.Success(this.trending)

    )

    // Get cached data
    suspend fun getCachedData(): NetworkRequest<PeoplePageData> {
        return try {
            // Try to get cached data
            val cachedData = local.getPeopleData().firstOrNull()
            if (cachedData != null) {
                NetworkRequest.Success(cachedData.toCombinedData())
            } else {
                NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            NetworkRequest.Error("Failed to load cached data: ${e.localizedMessage}")
        }
    }
}