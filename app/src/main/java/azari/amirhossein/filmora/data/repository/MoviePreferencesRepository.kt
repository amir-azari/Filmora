package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
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
            val response = remote.searchMovie(query)
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

}
