package azari.amirhossein.filmora.models.detail

import azari.amirhossein.filmora.models.ResponseLanguage

data class DetailMediaItem(
    val movie: ResponseMovieDetails? = null,
    val tv: ResponseTvDetails? = null,
    val credits: ResponseCredit? = null,
    val language: ResponseLanguage? = null,
    val similar: ResponseMovieSimilar? = null,
    val recommendations: ResponseMovieRecommendations? = null,
    val tvSimilar: ResponseTvSimilar? = null,
    val tvRecommendations: ResponseTvRecommendations? = null,
    val videos: ResponseVideo? = null,
    val images: ResponseImage? = null,
    val reviews: ResponseReviews? =null
)
