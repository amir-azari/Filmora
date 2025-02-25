package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails
import azari.amirhossein.filmora.utils.Constants

@Entity(tableName = Constants.Database.TV_DETAIL_TABLE)
data class TvDetailEntity(
    @PrimaryKey val id: Int = 0,
    val tv: ResponseTvDetails,
    val timestamp: Long = System.currentTimeMillis()

)