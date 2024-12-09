package azari.amirhossein.filmora.data.network

import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import azari.amirhossein.filmora.models.authentication.ResponseGuestSession
import azari.amirhossein.filmora.models.authentication.ResponseToken
import azari.amirhossein.filmora.models.authentication.ResponseSession
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServices {

    @GET("authentication/token/new")
    suspend fun requestToken(): Response<ResponseToken>

    @POST("authentication/token/validate_with_login")
    suspend fun validateWithLogin(
        @Body request : RequestLogin
    ): Response<ResponseToken>

    @POST("authentication/session/new")
    suspend fun createSession(
        @Body request: RequestSession
    ): Response<ResponseSession>

    @GET("authentication/guest_session/new")
    suspend fun createGuestSession(): Response<ResponseGuestSession>

}