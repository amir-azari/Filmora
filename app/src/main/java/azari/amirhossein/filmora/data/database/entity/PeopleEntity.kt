package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.celebtiry.ResponseTrendingCelebrity
import azari.amirhossein.filmora.utils.Constants

@Entity(tableName = Constants.Database.CELEBRITIES_TABLE)
data class PeopleEntity(
    @PrimaryKey val id: Int = 0,
    val popular: ResponsePopularCelebrity,
    val trending: ResponseTrendingCelebrity,

)