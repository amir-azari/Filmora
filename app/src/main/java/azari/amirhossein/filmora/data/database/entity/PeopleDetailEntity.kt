package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.utils.Constants

@Entity(tableName = Constants.Database.PEOPLE_DETAIL_TABLE)
data class PeopleDetailEntity(
    @PrimaryKey
    val id: Int = 0,
    val detail: ResponsePeopleDetails,
    val timestamp: Long = System.currentTimeMillis()

)