package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants

@Entity(tableName = Constants.Database.MOVIES_TABLE)
data class MovieEntity(
    @PrimaryKey val id: Int = 0,
    val trending: ResponseTrendingMovie,
    val movieGenres: ResponseGenresList,
    val popular: ResponseMoviesList,
    val nowPlaying: ResponseMoviesList,
    val topRated: ResponseMoviesList,
    val upcoming: ResponseMoviesList
)