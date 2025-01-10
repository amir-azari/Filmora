package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.utils.Constants

@Entity(tableName = Constants.Database.HOME_TABLE)
data class HomeEntity(
    @PrimaryKey val id: Int = 0,
    val trending: ResponseTrendingList,
    val movieGenres: ResponseGenresList,
    val tvGenres: ResponseGenresList,
    val recommendedMovies: ResponseMoviesList,
    val recommendedTvs: ResponseTvsList
)
