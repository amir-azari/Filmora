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
    suspend fun searchMovie(query:String) = api.searchMovie(query)
    suspend fun getMovieGenres() = api.getMovieGenres()
    suspend fun getMovieKeywords(movieId:String) = api.getMovieKeywords(movieId)
    suspend fun searchTv(query:String) = api.searchTv(query)
}