package azari.amirhossein.filmora.data.network

import azari.amirhossein.filmora.models.ResponseLanguage
import azari.amirhossein.filmora.models.acccount.FavoriteRequest
import azari.amirhossein.filmora.models.acccount.ResponseAccountDetails
import azari.amirhossein.filmora.models.acccount.ResponseDefault
import azari.amirhossein.filmora.models.acccount.WatchlistRequest
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import azari.amirhossein.filmora.models.authentication.ResponseGuestSession
import azari.amirhossein.filmora.models.authentication.ResponseSession
import azari.amirhossein.filmora.models.authentication.ResponseToken
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.celebtiry.ResponseTrendingCelebrity
import azari.amirhossein.filmora.models.detail.RateRequest
import azari.amirhossein.filmora.models.detail.ResponseAccountStates
import azari.amirhossein.filmora.models.detail.movie.ResponseCollectionDetails
import azari.amirhossein.filmora.models.detail.movie.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails
import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMovieKeywordList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvKeywordList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.models.tv.ResponseTrendingTv
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiServices {

    // ---------Authentication---------
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

    //---------Profile(Account)---------
    @GET("account")
    suspend fun getAccountDetails(@Query("session_id") sessionId: String): Response<ResponseAccountDetails>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(@Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1
    ): Response<ResponseMoviesList>

    @GET("account/{account_id}/favorite/tv")
    suspend fun getFavoriteTvs(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1,
    ): Response<ResponseTvsList>


    @GET("account/{account_id}/watchlist/movies")
    suspend fun getWatchlistMovies(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1,
    ): Response<ResponseMoviesList>

    @GET("account/{account_id}/watchlist/tv")
    suspend fun getWatchlistTvs(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1,
    ): Response<ResponseTvsList>

    @GET("account/{account_id}/rated/movies")
    suspend fun getRatedMovies(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1,
    ): Response<ResponseMoviesList>

    @GET("account/{account_id}/rated/tv")
    suspend fun getRatedTvs(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1,
    ): Response<ResponseTvsList>

    @POST("account/{account_id}/favorite")
    suspend fun markAsFavorite(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Body requestFavorite: FavoriteRequest
    ): Response<ResponseDefault>

    @POST("account/{account_id}/watchlist")
    suspend fun markWatchlist(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Body requestWatchlist: WatchlistRequest
    ): Response<ResponseDefault>

    @POST("movie/{movie_id}/rating")
    suspend fun rateMovie(
        @Path("movie_id") movieId: Int,
        @Query("session_id") sessionId: String,
        @Body rating: RateRequest
    ): Response<ResponseDefault>

    @DELETE("movie/{movie_id}/rating")
    suspend fun deleteMovieRating(
        @Path("movie_id") movieId: Int,
        @Query("session_id") sessionId: String
    ): Response<ResponseDefault>

    @POST("tv/{series_id}/rating")
    suspend fun rateTv(
        @Path("series_id") seriesId: Int,
        @Query("session_id") sessionId: String,
        @Body rating: RateRequest
    ): Response<ResponseDefault>

    @DELETE("tv/{series_id}/rating")
    suspend fun deleteTvRating(
        @Path("series_id") seriesId: Int,
        @Query("session_id") sessionId: String
    ): Response<ResponseDefault>

    //---------Movies---------

    // Search Movie
    @GET("search/movie")
    suspend fun searchMovie(@Query("page") page: Int, @Query("query") query: String) : Response<ResponseMoviesList>

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
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int , @QueryMap params: Map<String, String>): Response<ResponseMovieDetails>

    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovie(@Path("time_window") timeWindow: String , @Query("page") page: Int): Response<ResponseTrendingMovie>

    @GET("movie/popular")
    suspend fun getPopularMovie(@Query("page") page: Int): Response<ResponseMoviesList>

    @GET("movie/now_playing")
    suspend fun getNowPlaying(@Query("page") page: Int): Response<ResponseMoviesList>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovie(@Query("page") page: Int): Response<ResponseMoviesList>

    @GET("movie/upcoming")
    suspend fun getUpcoming(@Query("page") page: Int): Response<ResponseMoviesList>

    @GET("collection/{collection_id}")
    suspend fun getCollectionDetail (@Path("collection_id")collectionId: Int): Response<ResponseCollectionDetails>

    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieAccountStates(
        @Path("movie_id") movieId: Int,
        @Query("session_id") sessionId: String
    ): Response<ResponseAccountStates>

    //---------TVs---------

    // Search TV
    @GET("search/tv")
    suspend fun searchTv(@Query("page") page: Int, @Query("query") query: String) : Response<ResponseTvsList>

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
    suspend fun getDetailsTvSeries(@Path("series_id") seriesId: Int , @QueryMap params: Map<String, String>): Response<ResponseTvDetails>

    @GET("trending/tv/{time_window}")
    suspend fun getTrendingTv(@Path("time_window") timeWindow: String , @Query("page") page: Int): Response<ResponseTrendingTv>

    @GET("tv/popular")
    suspend fun getPopularTv(@Query("page") page: Int): Response<ResponseTvsList>

    @GET("tv/airing_today")
    suspend fun getNowAiringToday(@Query("page") page: Int): Response<ResponseTvsList>

    @GET("tv/top_rated")
    suspend fun getTopRatedTv(@Query("page") page: Int): Response<ResponseTvsList>

    @GET("tv/on_the_air")
    suspend fun getOnTheAir(@Query("page") page: Int): Response<ResponseTvsList>

    @GET("tv/{series_id}/account_states")
    suspend fun getTvAccountStates(
        @Path("series_id") seriesId: Int,
        @Query("session_id") sessionId: String
    ): Response<ResponseAccountStates>

    //---------People---------

    @GET("person/popular")
    suspend fun getPopularPeople(@Query("page") page: Int): Response<ResponsePopularCelebrity>

    @GET("trending/person/{time_window}")
    suspend fun geTrendingPeople(@Path("time_window") timeWindow: String ,@Query("page") page: Int): Response<ResponseTrendingCelebrity>

    @GET("person/{person_id}")
    suspend fun getPeopleDetails(@Path("person_id") personId: Int, @QueryMap params: Map<String, String>): Response<ResponsePeopleDetails>

    @GET("search/person")
    suspend fun searchPeople(@Query("page") page: Int ,@Query("query") query: String): Response<ResponsePopularCelebrity>
    //---------All(Movie , Tv , People)---------

    // Trending
    @GET("trending/all/{time_window}")
    suspend fun getTrending(@Path("time_window") timeWindow: String): Response<ResponseTrendingList>

    // Configuration
    @GET("configuration/languages")
    suspend fun getLanguage(): Response<ResponseLanguage>
}