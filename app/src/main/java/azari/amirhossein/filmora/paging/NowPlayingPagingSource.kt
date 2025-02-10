package azari.amirhossein.filmora.paging

import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList

class NowPlayingPagingSource(
    private val remote: RemoteDataSource,
    sessionManager: SessionManager
) : BasePagingSource<ResponseMovieType>(sessionManager , MediaType.MOVIE) {

    override suspend fun fetchData(page: Int, preferences: TvAndMoviePreferences): List<ResponseMovieType>? {
        val response = remote.getNowPlaying(page)
        return if (response.isSuccessful) {
            response.body()?.results?.map { ResponseMovieType.Movies(it) }
        } else {
            null
        }
    }
}

