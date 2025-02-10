package azari.amirhossein.filmora.models.tv

import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList

sealed class ResponseTvType{
    data class Trending(val result: ResponseTrendingTv.Result) : ResponseTvType()
    data class Tvs(val result: ResponseTvsList.Result) : ResponseTvType()
}