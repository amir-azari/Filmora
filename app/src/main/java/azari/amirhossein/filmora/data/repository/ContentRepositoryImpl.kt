package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.models.GenreResponse
import azari.amirhossein.filmora.data.models.KeywordResponse
import azari.amirhossein.filmora.data.models.movie.MovieResponse
import azari.amirhossein.filmora.data.models.tv.TvShowResponse
import azari.amirhossein.filmora.data.network.ApiServices
import azari.amirhossein.filmora.domain.repository.ContentRepository
import azari.amirhossein.filmora.utils.extensions.toResource
import azari.amirhossein.filmora.utils.network.Failure
import azari.amirhossein.filmora.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl@Inject constructor(private val api: ApiServices ) : ContentRepository {

    // --- Movies ---
    override fun searchMovies(query: String, page: Int): Flow<Resource<MovieResponse>> = flow {
        emit(Resource.Loading)
        val response = api.searchMovies(page, query)
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)

    override fun getMovieGenres(): Flow<Resource<GenreResponse>> = flow {
        emit(Resource.Loading)
        val response = api.getMovieGenres()
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)

    override fun getMovieKeywords(movieId: Int): Flow<Resource<KeywordResponse>> = flow {
        emit(Resource.Loading)
        val response = api.getMovieKeywords(movieId.toString())
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)

    override fun discoverMovies(params: Map<String, String>): Flow<Resource<MovieResponse>> =flow {
        emit(Resource.Loading)
        val response = api.discoverMovies(params)
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)

    // --- TV Shows ---
    override fun searchTvShows(query: String, page: Int): Flow<Resource<TvShowResponse>> = flow {
        emit(Resource.Loading)
        val response = api.searchTvShows(page, query)
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)

    override fun getTvGenres(): Flow<Resource<GenreResponse>> = flow {
        emit(Resource.Loading)
        val response = api.getTvGenres()
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)

    override fun getTvKeywords(tvId: Int): Flow<Resource<KeywordResponse>> = flow {
        emit(Resource.Loading)
        val response = api.getTvKeywords(tvId)
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)

    override fun discoverTvShows(params: Map<String, String>): Flow<Resource<TvShowResponse>> = flow {
        emit(Resource.Loading)
        val response = api.discoverTvShows(params)
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)


    private fun <T> Flow<Resource<T>>.handleErrors(): Flow<Resource<T>> {
        return this.catch { e ->
            val failure = when (e) {
                is IOException -> Failure.NetworkConnection
                is HttpException -> Failure.ServerError
                else -> Failure.UnknownError
            }
            emit(Resource.Error(failure))
        }.flowOn(Dispatchers.IO)
    }
}