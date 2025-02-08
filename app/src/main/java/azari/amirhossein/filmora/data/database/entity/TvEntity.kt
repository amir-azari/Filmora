package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.models.tv.ResponseTrendingTv
import azari.amirhossein.filmora.utils.Constants

@Entity(tableName = Constants.Database.TV_SHOW_TABLE)
data class TvEntity(
    @PrimaryKey val id: Int = 0,
    val trending: ResponseTrendingTv,
    val tvGenres: ResponseGenresList,
    val popular: ResponseTvsList,
    val topRated: ResponseTvsList,
    val airingToday: ResponseTvsList,
    val onTheAir: ResponseTvsList

)