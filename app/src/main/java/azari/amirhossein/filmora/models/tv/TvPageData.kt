package azari.amirhossein.filmora.models.tv

import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.utils.NetworkRequest

data class TvPageData(
    val trending: NetworkRequest<ResponseTrendingTv>,
    val tvGenres: NetworkRequest<ResponseGenresList>,
    val popular: NetworkRequest<ResponseTvsList>,
    val airingToday: NetworkRequest<ResponseTvsList>,
    val topRated: NetworkRequest<ResponseTvsList>,
    val onTheAir: NetworkRequest<ResponseTvsList>
)