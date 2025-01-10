package azari.amirhossein.filmora.models

import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.utils.NetworkRequest

data class HomePageData(
    val trending: NetworkRequest<ResponseTrendingList>,
    val movieGenres: NetworkRequest<ResponseGenresList>,
    val tvGenres: NetworkRequest<ResponseGenresList>,
    val recommendedMovies: NetworkRequest<ResponseMoviesList>,
    val recommendedTvs: NetworkRequest<ResponseTvsList>
)