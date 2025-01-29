package azari.amirhossein.filmora.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import azari.amirhossein.filmora.data.database.entity.DetailEntity
import azari.amirhossein.filmora.data.database.entity.HomeEntity


@Database(entities = [HomeEntity::class , DetailEntity::class], version = 4, exportSchema = false)
@TypeConverters(ResponseTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ado(): AppDao
}

