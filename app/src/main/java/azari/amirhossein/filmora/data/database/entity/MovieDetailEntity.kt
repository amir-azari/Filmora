package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import azari.amirhossein.filmora.models.detail.movie.ResponseMovieDetails
import azari.amirhossein.filmora.utils.Constants

@Entity(tableName = Constants.Database.MOVIE_DETAIL_TABLE)
data class MovieDetailEntity(
    @PrimaryKey val id: Int = 0,
    val movie: ResponseMovieDetails,
    val timestamp: Long = System.currentTimeMillis()

)