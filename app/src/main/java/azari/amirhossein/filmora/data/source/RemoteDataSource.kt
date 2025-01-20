package azari.amirhossein.filmora.data.source

import azari.amirhossein.filmora.data.network.ApiServices
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val api : ApiServices) {
    suspend fun requestToken() = api.requestToken()
    suspend fun validateWithLogin( request: RequestLogin) = api.validateWithLogin(request)
    suspend fun createSession( request: RequestSession) = api.createSession(request)
    suspend fun createGuestSession() = api.createGuestSession()
    //---------Movies---------

    suspend fun searchMovie(query:String) = api.searchMovie(query)
    suspend fun getMovieGenres() = api.getMovieGenres()
    suspend fun getMovieKeywords(movieId:String) = api.getMovieKeywords(movieId)
    suspend fun discoverMovies(params: Map<String, String>) = api.discoverMovies(params)
    suspend fun getMovieDetails(movieId: Int) = api.getMovieDetails(movieId)
    suspend fun getMovieCredits(movieId: Int) = api.getMovieCredits(movieId)

    //---------TVs---------

    suspend fun searchTv(query:String) = api.searchTv(query)
    suspend fun getTvGenres() = api.getTvGenres()
    suspend fun getTvKeywords(tvId:Int) = api.getTvKeywords(tvId)
    suspend fun discoverTvShows(params: Map<String, String>) = api.discoverTvShows(params)
    suspend fun getDetailsTvSeries(seriesId: Int) = api.getDetailsTvSeries(seriesId)
    suspend fun getTvCredits(seriesId: Int) = api.getTvCredits(seriesId)

    //---------All(Movie , Tv , People)---------
    suspend fun getTrending(timeWindow:String) = api.getTrending(timeWindow)

    //---------Configuration---------
    suspend fun getLanguage() = api.getLanguage()

}