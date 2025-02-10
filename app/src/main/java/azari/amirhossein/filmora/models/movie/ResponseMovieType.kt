package azari.amirhossein.filmora.models.movie

import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList

sealed class ResponseMovieType{
    data class Trending(val result: ResponseTrendingMovie.Result) : ResponseMovieType()
    data class Movies(val result: ResponseMoviesList.Result) : ResponseMovieType()
}