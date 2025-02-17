package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.database.entity.MediaDetailEntity
import azari.amirhossein.filmora.data.source.LocalDataSource
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.detail.DetailMediaItem
import azari.amirhossein.filmora.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MediaDetailsRepository @Inject constructor(private val remote: RemoteDataSource, private val local: LocalDataSource) {

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
                        .zip(flow { emit(remote.getMovieVideos(id)) }) { (details, credits, similar, recommendations), videosResponse ->
                            Quintuple(
                                details,
                                credits,
                                similar,
                                recommendations,
                                NetworkResponse(videosResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getMovieImages(id)) }) { (details, credits, similar, recommendations, videos), imagesResponse ->
                            Sixtuple(
                                details,
                                credits,
                                similar,
                                recommendations,
                                videos,
                                NetworkResponse(imagesResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getLanguage()) }) { (details, credits, similar, recommendations, videos, images), languageResponse ->
                            Septuple(
                                details,
                                credits,
                                similar,
                                recommendations,
                                videos,
                                images,
                                NetworkResponse(languageResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getMovieReviews(id)) }) { (details, credits, similar, recommendations, videos, images , language), reviewsResponse ->
                            DetailMediaItem(
                                movie = (details as? NetworkRequest.Success)?.data,
                                credits = (credits as? NetworkRequest.Success)?.data,
                                similar = (similar as? NetworkRequest.Success)?.data,
                                recommendations = (recommendations as? NetworkRequest.Success)?.data,
                                videos = (videos as? NetworkRequest.Success)?.data,
                                images = (images as? NetworkRequest.Success)?.data,
                                language = (language as? NetworkRequest.Success)?.data,
                                reviews = (NetworkResponse(reviewsResponse).handleNetworkResponse() as? NetworkRequest.Success)?.data
                            )
                        }
                        .collect { mediaItem ->
                            local.saveMediaDetail(mediaItem.toEntity())
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
                        .zip(flow { emit(remote.getTvVideos(id)) }) { (details, credits, similar, recommendations), videosResponse ->
                            Quintuple(
                                details,
                                credits,
                                similar,
                                recommendations,
                                NetworkResponse(videosResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getTvImages(id)) }) { (details, credits, similar, recommendations, videos), imagesResponse ->
                            Sixtuple(
                                details,
                                credits,
                                similar,
                                recommendations,
                                videos,
                                NetworkResponse(imagesResponse).handleNetworkResponse()
                            )
                        }
                        .zip(flow { emit(remote.getLanguage()) }) { (details, credits, similar, recommendations, videos, images), languageResponse ->
                            Septuple(
                                details,
                                credits,
                                similar,
                                recommendations,
                                videos,
                                images,
                                NetworkResponse(languageResponse).handleNetworkResponse()
                            )
                        }

                        .zip(flow { emit(remote.getTvReviews(id)) }) { (details, credits, similar, recommendations, videos, images, language), reviewsResponse ->
                            DetailMediaItem(
                                tv = (details as? NetworkRequest.Success)?.data,
                                credits = (credits as? NetworkRequest.Success)?.data,
                                tvSimilar = (similar as? NetworkRequest.Success)?.data,
                                tvRecommendations = (recommendations as? NetworkRequest.Success)?.data,
                                videos = (videos as? NetworkRequest.Success)?.data,
                                images = (images as? NetworkRequest.Success)?.data,
                                language = (language as? NetworkRequest.Success)?.data,
                                reviews = (NetworkResponse(reviewsResponse).handleNetworkResponse() as? NetworkRequest.Success)?.data
                            )
                        }
                        .collect { mediaItem ->
                            local.saveMediaDetail(mediaItem.toEntity())
                            emit(NetworkRequest.Success(mediaItem))
                        }
                }
            }
        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    private fun MediaDetailEntity.toDetailMediaItem() = DetailMediaItem(
        movie = this.movie,
        tv = this.tv,
        credits = this.credits,
        language = this.language,
        similar = this.similar,
        recommendations = this.recommendations,
        tvSimilar = this.tvSimilar,
        tvRecommendations = this.tvRecommendations,
        videos = this.videos,
        images = this.images,
        reviews = this.reviews
    )

    private fun DetailMediaItem.toEntity() = MediaDetailEntity(
        id = this.movie?.id ?: this.tv?.id ?: 0,
        movie = this.movie,
        tv = this.tv,
        credits = this.credits,
        language = this.language,
        similar = this.similar,
        recommendations = this.recommendations,
        tvSimilar = this.tvSimilar,
        tvRecommendations = this.tvRecommendations,
        videos = this.videos,
        images = this.images,
        reviews = this.reviews
    )
    suspend fun deleteExpiredDetails(expirationTime: Long) {
        local.deleteExpiredMediaDetailData(expirationTime)
    }

    // Get cached data
    suspend fun getCachedData(id :Int): NetworkRequest<DetailMediaItem> {
        return try {
            // Try to get cached data
            val cachedData = local.getMediaDetailById(id).firstOrNull()
            if (cachedData != null) {
                NetworkRequest.Success(cachedData.toDetailMediaItem())
            } else {
                NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            NetworkRequest.Error("Failed to load cached data: ${e.localizedMessage}")
        }
    }
}
