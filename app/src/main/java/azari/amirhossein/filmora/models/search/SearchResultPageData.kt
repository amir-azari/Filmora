package azari.amirhossein.filmora.models.search

import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList

data class SearchResultPageData(
    val movies: ResponseMoviesList? = null,
    val tvShows: ResponseTvsList? = null,
    val people: ResponsePopularCelebrity? = null,
)