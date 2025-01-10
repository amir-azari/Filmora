package azari.amirhossein.filmora.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Home
    @Query("SELECT * FROM ${Constants.Database.HOME_TABLE} WHERE id = 0 LIMIT 1")
    fun getHomeData(): Flow<HomeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHomeData(data: HomeEntity)
}
