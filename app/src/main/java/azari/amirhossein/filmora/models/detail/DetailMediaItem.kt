package azari.amirhossein.filmora.models.detail

import azari.amirhossein.filmora.models.ResponseLanguage

data class DetailMediaItem(
    val movie: ResponseMovieDetails? = null,
    val tv: ResponseTvDetails? = null,
    val credits: ResponseCredit? = null,
    val language: ResponseLanguage? = null
)
