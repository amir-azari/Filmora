package azari.amirhossein.filmora.models.home

import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.models.detail.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.ResponseTvDetails

data class HomeMediaItem(
    val movie: ResponseMovieDetails? = null,
    val tv: ResponseTvDetails? = null,
    val credits: ResponseCredit? = null
)
