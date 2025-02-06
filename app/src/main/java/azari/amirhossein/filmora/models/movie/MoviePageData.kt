package azari.amirhossein.filmora.models.movie

import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.NetworkRequest

data class MoviePageData(
    val trending: NetworkRequest<ResponseTrendingMovie>,
    val movieGenres: NetworkRequest<ResponseGenresList>,
    val popular : NetworkRequest<ResponseMoviesList>,
    val nowPlaying : NetworkRequest<ResponseMoviesList>,
    val topRated : NetworkRequest<ResponseMoviesList>,
    val upcoming : NetworkRequest<ResponseMoviesList>,


)
