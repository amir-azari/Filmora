package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleType
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie

class TrendingPeoplePagingSource(
    private val remote: RemoteDataSource,
    sessionManager: SessionManager
) : BasePagingSource<ResponsePeopleType>(sessionManager ,MediaType.PEOPLE) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<ResponsePeopleType>? {
        val response = remote.getTrendingPeoples("day", page)
        return if (response.isSuccessful) {
            response.body()?.results?.map { ResponsePeopleType.Trending(it) }
        } else {
            null
        }
    }
}