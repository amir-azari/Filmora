package azari.amirhossein.filmora.domain.repository

import azari.amirhossein.filmora.data.models.GenreResponse
import azari.amirhossein.filmora.data.models.KeywordResponse
import azari.amirhossein.filmora.data.models.movie.MovieResponse
import azari.amirhossein.filmora.data.models.tv.TvShowResponse
import azari.amirhossein.filmora.utils.network.Resource
import kotlinx.coroutines.flow.Flow

interface ContentRepository {

    // --- Movies ---
    fun searchMovies(query: String, page: Int = 1): Flow<Resource<MovieResponse>>
    fun getMovieGenres(): Flow<Resource<GenreResponse>>
    fun getMovieKeywords(movieId: Int): Flow<Resource<KeywordResponse>>
    fun discoverMovies(params: Map<String, String>): Flow<Resource<MovieResponse>>


    // --- TV Shows ---
    fun searchTvShows(query: String, page: Int = 1): Flow<Resource<TvShowResponse>>
    fun getTvGenres(): Flow<Resource<GenreResponse>>
    fun getTvKeywords(tvId: Int): Flow<Resource<KeywordResponse>>
    fun discoverTvShows(params: Map<String, String>): Flow<Resource<TvShowResponse>>


}
