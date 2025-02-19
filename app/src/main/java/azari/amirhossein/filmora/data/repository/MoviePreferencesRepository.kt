package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMovieKeywordList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MoviePreferencesRepository @Inject constructor(private val remote: RemoteDataSource) {
    fun searchMovies(query: String): Flow<NetworkRequest<ResponseMoviesList>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val response = remote.searchMovie(1,query)
            val networkResponse = NetworkResponse(response).handleNetworkResponse()
            if (networkResponse is NetworkRequest.Success) {
                val movies = networkResponse.data
                movies?.let {
                    emit(NetworkRequest.Success(it))
                } ?: emit(NetworkRequest.Error("Empty data received"))
            } else {
                throw Exception((networkResponse as NetworkRequest.Error).message)
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    fun getMovieGenres(): Flow<NetworkRequest<ResponseGenresList>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val response = remote.getMovieGenres()
            val networkResponse = NetworkResponse(response).handleNetworkResponse()
            if (networkResponse is NetworkRequest.Success) {
                val genres = networkResponse.data
                genres?.let {
                    emit(NetworkRequest.Success(it))
                } ?: emit(NetworkRequest.Error("Empty data received"))
            } else {
                throw Exception((networkResponse as NetworkRequest.Error).message)
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getMovieKeywords(movieId: String): Flow<NetworkRequest<ResponseMovieKeywordList>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val response = remote.getMovieKeywords(movieId)
            when (val networkResponse = NetworkResponse(response).handleNetworkResponse()) {
                is NetworkRequest.Success -> {
                    emit(networkResponse)
                }
                is NetworkRequest.Error -> {
                    emit(networkResponse)
                }
                else -> emit(NetworkRequest.Error("Unknown error"))
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}
