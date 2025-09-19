package azari.amirhossein.filmora.data.network

import azari.amirhossein.filmora.data.models.GenreResponse
import azari.amirhossein.filmora.data.models.KeywordResponse
import azari.amirhossein.filmora.data.models.movie.MovieResponse
import azari.amirhossein.filmora.data.models.authentication.CreateGuestSession
import azari.amirhossein.filmora.data.models.authentication.CreateSessionRequest
import azari.amirhossein.filmora.data.models.authentication.CreateSessionResponse
import azari.amirhossein.filmora.data.models.authentication.RequestTokenResponse
import azari.amirhossein.filmora.data.models.authentication.ValidateLoginRequest
import azari.amirhossein.filmora.data.models.tv.TvShowResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiServices {

    // ---------Authentication---------
    @GET("authentication/token/new")
    suspend fun createRequestToken(): Response<RequestTokenResponse>

    @POST("authentication/token/validate_with_login")
    suspend fun validateLogin(@Body request : ValidateLoginRequest): Response<RequestTokenResponse>

    @POST("authentication/session/new")
    suspend fun createSession(@Body request: CreateSessionRequest): Response<CreateSessionResponse>

    @GET("authentication/guest_session/new")
    suspend fun createGuestSession(): Response<CreateGuestSession>

    //---------Movies---------

    // Search Movie
    @GET("search/movie")
    suspend fun searchMovies(@Query("page") page: Int, @Query("query") query: String) : Response<MovieResponse>

    // Movie genres
    @GET("genre/movie/list")
    suspend fun getMovieGenres() : Response<GenreResponse>

    // Movie keywords
    @GET("movie/{movie_id}/keywords")
    suspend fun getMovieKeywords( @Path("movie_id") movieId: String): Response<KeywordResponse>

    // Discover
    @GET("discover/movie")
    suspend fun discoverMovies(@QueryMap params: Map<String, String>): Response<MovieResponse>

    //---------TVs---------

    // Search TV
    @GET("search/tv")
    suspend fun searchTvShows(@Query("page") page: Int, @Query("query") query: String) : Response<TvShowResponse>

    // Tv genres
    @GET("genre/tv/list")
    suspend fun getTvGenres() : Response<GenreResponse>

    // Tv keywords
    @GET("tv/{series_id}/keywords")
    suspend fun getTvKeywords( @Path("series_id") tvId: Int): Response<KeywordResponse>

    // Discover
    @GET("discover/tv")
    suspend fun discoverTvShows(@QueryMap params: Map<String, String>): Response<TvShowResponse>


}