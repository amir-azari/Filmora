package azari.amirhossein.filmora.data.source

import azari.amirhossein.filmora.data.network.ApiServices
import azari.amirhossein.filmora.models.acccount.FavoriteRequest
import azari.amirhossein.filmora.models.acccount.WatchlistRequest
import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import azari.amirhossein.filmora.models.detail.RateRequest
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val api : ApiServices) {

    //---------Authentication---------

    suspend fun requestToken() = api.requestToken()
    suspend fun validateWithLogin( request: RequestLogin) = api.validateWithLogin(request)
    suspend fun createSession( request: RequestSession) = api.createSession(request)
    suspend fun createGuestSession() = api.createGuestSession()

    //---------Profile(Account)---------
    suspend fun getAccountDetails(sessionId : String) = api.getAccountDetails(sessionId)

    suspend fun getFavoriteMovies(accountId: Int, sessionId: String, page: Int) = api.getFavoriteMovies(accountId, sessionId, page)
    suspend fun getFavoriteTV(accountId: Int, sessionId: String, page: Int) = api.getFavoriteTvs(accountId, sessionId, page)

    suspend fun getWatchlistMovies(accountId: Int, sessionId: String, page: Int) = api.getWatchlistMovies(accountId, sessionId, page)
    suspend fun getWatchlistTvs(accountId: Int, sessionId: String, page: Int) = api.getWatchlistTvs(accountId, sessionId, page)

    suspend fun getRatedMovies(accountId: Int, sessionId: String, page: Int) = api.getRatedMovies(accountId, sessionId, page)
    suspend fun getRatedTvs(accountId: Int, sessionId: String, page: Int) = api.getRatedTvs(accountId, sessionId, page)

    suspend fun markAsFavorite(accountId: Int, sessionId: String, requestFavorite: FavoriteRequest) = api.markAsFavorite(accountId, sessionId, requestFavorite)
    suspend fun markWatchlist(accountId: Int, sessionId: String, requestWatchlist: WatchlistRequest) = api.markWatchlist(accountId, sessionId, requestWatchlist)

    //---------Movies---------

    suspend fun searchMovie(page :Int ,query:String) = api.searchMovie(page,query)
    suspend fun getMovieGenres() = api.getMovieGenres()
    suspend fun getMovieKeywords(movieId:String) = api.getMovieKeywords(movieId)
    suspend fun discoverMovies(params: Map<String, String>) = api.discoverMovies(params)
    suspend fun getMovieDetails(movieId: Int,params: Map<String, String>) = api.getMovieDetails(movieId,params)
    suspend fun getTrendingMovie(timeWindow:String ,page : Int) = api.getTrendingMovie(timeWindow , page)
    suspend fun getPopularMovie(page : Int) = api.getPopularMovie(page)
    suspend fun getNowPlaying(page : Int) = api.getNowPlaying(page)
    suspend fun getTopRatedMovie(page : Int) = api.getTopRatedMovie(page)
    suspend fun getUpcoming(page : Int) = api.getUpcoming(page)
    suspend fun getMovieAccountStates(movieId: Int, sessionId: String) = api.getMovieAccountStates(movieId, sessionId)
    suspend fun addRateMovie(movieId: Int, sessionId: String, request: RateRequest) = api.rateMovie(movieId, sessionId, request)
    suspend fun deleteRating(movieId: Int, sessionId: String) = api.deleteRating(movieId, sessionId)

    //---------TVs---------

    suspend fun searchTv(page :Int ,query:String) = api.searchTv(page,query)
    suspend fun getTvGenres() = api.getTvGenres()
    suspend fun getTvKeywords(tvId:Int) = api.getTvKeywords(tvId)
    suspend fun discoverTvShows(params: Map<String, String>) = api.discoverTvShows(params)
    suspend fun getTvDetails(seriesId: Int, params: Map<String, String>) = api.getDetailsTvSeries(seriesId,params)
    suspend fun getTrendingTv(timeWindow:String,page : Int) = api.getTrendingTv(timeWindow ,page )
    suspend fun getPopularTv(page : Int) = api.getPopularTv(page)
    suspend fun getNowAiringToday(page : Int) = api.getNowAiringToday(page)
    suspend fun getTopRatedTv(page : Int) = api.getTopRatedTv(page)
    suspend fun getOnTheAir(page : Int) = api.getOnTheAir(page)
    suspend fun getCollectionDetail(collectionId : Int)= api.getCollectionDetail(collectionId)

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