package azari.amirhossein.filmora.data.network

import azari.amirhossein.filmora.models.ResponseLanguage
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import azari.amirhossein.filmora.models.authentication.ResponseGuestSession
import azari.amirhossein.filmora.models.authentication.ResponseToken
import azari.amirhossein.filmora.models.authentication.ResponseSession
import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.models.detail.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.ResponseTvDetails
import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMovieKeywordList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvKeywordList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiServices {

    // Authentication
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

    //---------Movies---------

    // Search Movie
    @GET("search/movie")
    suspend fun searchMovie( @Query("query") query: String) : Response<ResponseMoviesList>

    // Movie genres
    @GET("genre/movie/list")
    suspend fun getMovieGenres() : Response<ResponseGenresList>

    // Movie keywords
    @GET("movie/{movie_id}/keywords")
    suspend fun getMovieKeywords( @Path("movie_id") movieId: String): Response<ResponseMovieKeywordList>

    // Discover
    @GET("discover/movie")
    suspend fun discoverMovies(@QueryMap params: Map<String, String>): Response<ResponseMoviesList>

    // Detail
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): Response<ResponseMovieDetails>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Int): Response<ResponseCredit>
    //---------TVs---------

    // Search TV
    @GET("search/tv")
    suspend fun searchTv( @Query("query") query: String) : Response<ResponseTvsList>

    // Tv genres
    @GET("genre/tv/list")
    suspend fun getTvGenres() : Response<ResponseGenresList>

    // Tv keywords
    @GET("tv/{series_id}/keywords")
    suspend fun getTvKeywords( @Path("series_id") tvId: Int): Response<ResponseTvKeywordList>

    // Discover
    @GET("discover/tv")
    suspend fun discoverTvShows(@QueryMap params: Map<String, String>): Response<ResponseTvsList>

    // Detail
    @GET("tv/{series_id}")
    suspend fun getDetailsTvSeries(@Path("series_id") seriesId: Int): Response<ResponseTvDetails>

    @GET("tv/{series_id}/credits")
    suspend fun getTvCredits(@Path("series_id") seriesId: Int): Response<ResponseCredit>
    //---------All(Movie , Tv , People)---------

    // Trending
    @GET("trending/all/{time_window}")
    suspend fun getTrending(@Path("time_window") timeWindow: String): Response<ResponseTrendingList>

    // Configuration
    @GET("configuration/languages")
    suspend fun getLanguage(): Response<ResponseLanguage>
}