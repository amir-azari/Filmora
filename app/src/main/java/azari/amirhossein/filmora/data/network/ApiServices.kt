package azari.amirhossein.filmora.data.network

import azari.amirhossein.filmora.models.ResponseLanguage
import azari.amirhossein.filmora.models.detail.ResponseMovieSimilar
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import azari.amirhossein.filmora.models.authentication.ResponseGuestSession
import azari.amirhossein.filmora.models.authentication.ResponseToken
import azari.amirhossein.filmora.models.authentication.ResponseSession
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.celebtiry.ResponseTrendingCelebrity
import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.models.detail.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.ResponseMovieRecommendations
import azari.amirhossein.filmora.models.detail.ResponseReviews
import azari.amirhossein.filmora.models.detail.ResponseTvDetails
import azari.amirhossein.filmora.models.detail.ResponseTvRecommendations
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.models.detail.ResponseVideo
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

    @GET("movie/{movie_id}/similar")
    suspend fun getMovieSimilar (@Path("movie_id")movieId: Int): Response<ResponseMovieSimilar>

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations (@Path("movie_id")movieId: Int): Response<ResponseMovieRecommendations>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos (@Path("movie_id")movieId: Int): Response<ResponseVideo>

    @GET("movie/{movie_id}/images")
    suspend fun getMovieImages (@Path("movie_id")movieId: Int): Response<ResponseImage>

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews (@Path("movie_id")movieId: Int): Response<ResponseReviews>

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

    @GET("tv/{series_id}/similar")
    suspend fun getTvSimilar (@Path("series_id")seriesId: Int): Response<ResponseTvSimilar>

    @GET("tv/{series_id}/recommendations")
    suspend fun getTvRecommendations (@Path("series_id")seriesId: Int): Response<ResponseTvRecommendations>

    @GET("tv/{series_id}/videos")
    suspend fun getTvVideos (@Path("series_id")seriesId: Int): Response<ResponseVideo>

    @GET("tv/{series_id}/images")
    suspend fun getTvImages (@Path("series_id")seriesId: Int): Response<ResponseImage>

    @GET("tv/{series_id}/reviews")
    suspend fun getTvReviews (@Path("series_id")seriesId: Int): Response<ResponseReviews>

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

    //---------People---------

    @GET("person/popular")
    suspend fun getPopularPeople(@Query("page") page: Int): Response<ResponsePopularCelebrity>

    @GET("trending/person/{time_window}")
    suspend fun geTrendingPeople(@Path("time_window") timeWindow: String ,@Query("page") page: Int): Response<ResponseTrendingCelebrity>

    //---------All(Movie , Tv , People)---------

    // Trending
    @GET("trending/all/{time_window}")
    suspend fun getTrending(@Path("time_window") timeWindow: String): Response<ResponseTrendingList>

    // Configuration
    @GET("configuration/languages")
    suspend fun getLanguage(): Response<ResponseLanguage>
}