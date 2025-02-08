package azari.amirhossein.filmora.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import azari.amirhossein.filmora.data.database.entity.DetailEntity
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.PeopleEntity
import azari.amirhossein.filmora.data.database.entity.TvEntity


@Database(
    entities = [HomeEntity::class, MovieEntity::class, DetailEntity::class , PeopleEntity::class , TvEntity::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(ResponseTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ado(): AppDao
}

