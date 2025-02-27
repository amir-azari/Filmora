package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import azari.amirhossein.filmora.models.detail.ResponseAccountStates
import azari.amirhossein.filmora.utils.Constants
@Entity(tableName = Constants.Database.MOVIE_ACCOUNT_STATES_TABLE)
data class MovieAccountStatesEntity(
    @PrimaryKey val id: Int,
    val accountStates: ResponseAccountStates,
    val timestamp: Long = System.currentTimeMillis()
)