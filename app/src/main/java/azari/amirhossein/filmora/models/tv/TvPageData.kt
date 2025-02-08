package azari.amirhossein.filmora.models.tv

import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.utils.NetworkRequest

data class TvPageData(
    val trending: NetworkRequest<ResponseTrendingTv>,
    val tvGenres: NetworkRequest<ResponseGenresList>,

    )