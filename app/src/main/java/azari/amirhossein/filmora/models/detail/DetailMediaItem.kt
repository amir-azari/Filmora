package azari.amirhossein.filmora.models.detail

data class DetailMediaItem(
    val movie: ResponseMovieDetails? = null,
    val tv: ResponseTvDetails? = null,
    val credits: ResponseCredit? = null
)
