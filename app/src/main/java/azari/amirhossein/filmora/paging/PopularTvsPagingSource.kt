package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.tv.ResponseTvType

class PopularTvsPagingSource(
    private val remote: RemoteDataSource,
    sessionManager: SessionManager
) : BasePagingSource<ResponseTvType>(sessionManager , MediaType.TV) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<ResponseTvType>? {
        val response = remote.getPopularTv(page)
        return if (response.isSuccessful) {
            response.body()?.results?.map { ResponseTvType.Tvs(it) }
        } else {
            null
        }
    }
}