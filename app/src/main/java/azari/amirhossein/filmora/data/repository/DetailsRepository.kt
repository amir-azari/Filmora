package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.ResponseLanguage
import azari.amirhossein.filmora.models.detail.DetailMediaItem
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class DetailsRepository @Inject constructor(private val remote: RemoteDataSource) {

    fun getMediaDetails(id: Int, type: String): Flow<NetworkRequest<DetailMediaItem>> = flow {
        emit(NetworkRequest.Loading())

        try {

            when (type) {
                Constants.MediaType.MOVIE -> {
                    flow { emit(remote.getMovieDetails(id)) }
                        .zip(flow { emit(remote.getMovieCredits(id)) }) { detailsResponse, creditsResponse ->
                            val movieDetails = NetworkResponse(detailsResponse).handleNetworkResponse()
                            val credits = NetworkResponse(creditsResponse).handleNetworkResponse()

                            if (movieDetails is NetworkRequest.Success && credits is NetworkRequest.Success) {
                                NetworkRequest.Success(
                                    DetailMediaItem(movie = movieDetails.data, credits = credits.data)
                                )
                            } else {
                                val error = (movieDetails as? NetworkRequest.Error)?.message
                                    ?: (credits as? NetworkRequest.Error)?.message
                                    ?: Constants.Message.UNKNOWN_ERROR
                                NetworkRequest.Error(error)
                            }
                        }.collect { emit(it) }
                }

                Constants.MediaType.TV -> {
                    flow { emit(remote.getDetailsTvSeries(id)) }
                        .zip(flow { emit(remote.getTvCredits(id)) }) { detailsResponse, creditsResponse ->
                            val tvDetails = NetworkResponse(detailsResponse).handleNetworkResponse()
                            val credits = NetworkResponse(creditsResponse).handleNetworkResponse()

                            if (tvDetails is NetworkRequest.Success && credits is NetworkRequest.Success) {
                                NetworkRequest.Success(
                                    DetailMediaItem(tv = tvDetails.data, credits = credits.data)
                                )
                            } else {
                                val error = (tvDetails as? NetworkRequest.Error)?.message
                                    ?: (credits as? NetworkRequest.Error)?.message
                                    ?: Constants.Message.UNKNOWN_ERROR
                                NetworkRequest.Error(error)
                            }
                        }.collect { emit(it) }
                }

                else -> emit(NetworkRequest.Error(Constants.Message.INVALID_MEDIA_TYPE))
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))
        }
    }.flowOn(Dispatchers.IO)

    fun getLanguages(): Flow<NetworkRequest<ResponseLanguage>> = flow {
        emit(NetworkRequest.Loading())
        try {
            val languagesResponse = remote.getLanguage()
            val languages = NetworkResponse(languagesResponse).handleNetworkResponse()
            emit(languages)
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))
        }
    }.flowOn(Dispatchers.IO)
}

