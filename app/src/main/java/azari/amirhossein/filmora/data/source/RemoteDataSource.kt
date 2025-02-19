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

    suspend fun searchMovie(page :Int ,query:String) = api.searchMovie(page,query)
    suspend fun getMovieGenres() = api.getMovieGenres()
    suspend fun getMovieKeywords(movieId:String) = api.getMovieKeywords(movieId)
    suspend fun discoverMovies(params: Map<String, String>) = api.discoverMovies(params)
    suspend fun getMovieDetails(movieId: Int) = api.getMovieDetails(movieId)
    suspend fun getMovieCredits(movieId: Int) = api.getMovieCredits(movieId)
    suspend fun getMovieSimilar(movieId: Int) = api.getMovieSimilar(movieId)
    suspend fun getMovieRecommendations(movieId: Int) = api.getMovieRecommendations(movieId)
    suspend fun getMovieVideos(movieId: Int) = api.getMovieVideos(movieId)
    suspend fun getMovieImages(movieId: Int) = api.getMovieImages(movieId)
    suspend fun getMovieReviews(movieId: Int) = api.getMovieReviews(movieId)
    suspend fun getTrendingMovie(timeWindow:String ,page : Int) = api.getTrendingMovie(timeWindow , page)
    suspend fun getPopularMovie(page : Int) = api.getPopularMovie(page)
    suspend fun getNowPlaying(page : Int) = api.getNowPlaying(page)
    suspend fun getTopRatedMovie(page : Int) = api.getTopRatedMovie(page)
    suspend fun getUpcoming(page : Int) = api.getUpcoming(page)

    //---------TVs---------

    suspend fun searchTv(page :Int ,query:String) = api.searchTv(page,query)
    suspend fun getTvGenres() = api.getTvGenres()
    suspend fun getTvKeywords(tvId:Int) = api.getTvKeywords(tvId)
    suspend fun discoverTvShows(params: Map<String, String>) = api.discoverTvShows(params)
    suspend fun getDetailsTvSeries(seriesId: Int) = api.getDetailsTvSeries(seriesId)
    suspend fun getTvCredits(seriesId: Int) = api.getTvCredits(seriesId)
    suspend fun getTvSimilar(seriesId: Int) = api.getTvSimilar(seriesId)
    suspend fun getTvRecommendations(seriesId: Int) = api.getTvRecommendations(seriesId)
    suspend fun getTvVideos(seriesId: Int) = api.getTvVideos(seriesId)
    suspend fun getTvImages(seriesId: Int) = api.getTvImages(seriesId)
    suspend fun getTvReviews(seriesId: Int) = api.getTvReviews(seriesId)
    suspend fun getTrendingTv(timeWindow:String,page : Int) = api.getTrendingTv(timeWindow ,page )
    suspend fun getPopularTv(page : Int) = api.getPopularTv(page)
    suspend fun getNowAiringToday(page : Int) = api.getNowAiringToday(page)
    suspend fun getTopRatedTv(page : Int) = api.getTopRatedTv(page)
    suspend fun getOnTheAir(page : Int) = api.getOnTheAir(page)

    //---------Peoples---------
    suspend fun getPopularPeoples(page:Int) = api.getPopularPeople(page)
    suspend fun getTrendingPeoples(timeWindow:String,page:Int) = api.geTrendingPeople(timeWindow,page)
    suspend fun getPeopleDetails(personId:Int,params: Map<String, String>) = api.getPeopleDetails(personId,params)
    suspend fun searchPeople(page :Int ,query:String) = api.searchPeople(page,query)


    //---------All(Movie , Tv , People)---------
    suspend fun getTrending(timeWindow:String) = api.getTrending(timeWindow)

    //---------Configuration---------
    suspend fun getLanguage() = api.getLanguage()

}