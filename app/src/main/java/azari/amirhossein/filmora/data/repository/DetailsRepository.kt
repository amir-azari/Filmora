package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.detail.DetailMediaItem
import azari.amirhossein.filmora.models.detail.ResponseMovieRecommendations
import azari.amirhossein.filmora.models.detail.ResponseMovieSimilar
import azari.amirhossein.filmora.models.detail.ResponseTvRecommendations
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import azari.amirhossein.filmora.utils.Quadruple
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
                            Pair(
                                NetworkResponse(detailsResponse).handleNetworkResponse(),
                                NetworkResponse(creditsResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getMovieSimilar(id)) }) { (details, credits), similarResponse ->
                            Triple(
                                details,
                                credits,
                                NetworkResponse(similarResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getMovieRecommendations(id)) }) { (details, credits, similar), recommendationsResponse ->
                            Quadruple(
                                details,
                                credits,
                                similar,
                                NetworkResponse(recommendationsResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getLanguage()) }) { (details, credits, similar, recommendations), languageResponse ->
                            DetailMediaItem(
                                movie = (details as? NetworkRequest.Success)?.data,
                                credits = (credits as? NetworkRequest.Success)?.data,
                                similar = (similar as? NetworkRequest.Success)?.data,
                                recommendations = (recommendations as? NetworkRequest.Success)?.data,
                                language = (NetworkResponse(languageResponse).handleNetworkResponse() as? NetworkRequest.Success)?.data
                            )
                        }
                        .collect { mediaItem ->
                            emit(NetworkRequest.Success(mediaItem))
                        }
                }

                Constants.MediaType.TV -> {
                    flow { emit(remote.getDetailsTvSeries(id)) }
                        .zip(flow { emit(remote.getTvCredits(id)) }) { detailsResponse, creditsResponse ->
                            Pair(
                                NetworkResponse(detailsResponse).handleNetworkResponse(),
                                NetworkResponse(creditsResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getTvSimilar(id)) }) { (details, credits), similarResponse ->
                            Triple(
                                details,
                                credits,
                                NetworkResponse(similarResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getTvRecommendations(id)) }) { (details, credits, similar), recommendationsResponse ->
                            Quadruple(
                                details,
                                credits,
                                similar,
                                NetworkResponse(recommendationsResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getLanguage()) }) { (details, credits, similar, recommendations), languageResponse ->
                            DetailMediaItem(
                                tv = (details as? NetworkRequest.Success)?.data,
                                credits = (credits as? NetworkRequest.Success)?.data,
                                tvSimilar = (similar as? NetworkRequest.Success)?.data,
                                tvRecommendations = (recommendations as? NetworkRequest.Success)?.data,
                                language = (NetworkResponse(languageResponse).handleNetworkResponse() as? NetworkRequest.Success)?.data
                            )
                        }
                        .collect { mediaItem ->
                            emit(NetworkRequest.Success(mediaItem))
                        }
                }

                else -> emit(NetworkRequest.Error(Constants.Message.INVALID_MEDIA_TYPE))
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: Constants.Message.UNKNOWN_ERROR))
        }
    }.flowOn(Dispatchers.IO)
}

