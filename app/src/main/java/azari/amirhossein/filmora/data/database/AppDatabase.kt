package azari.amirhossein.filmora.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import azari.amirhossein.filmora.data.database.entity.MediaDetailEntity
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.PeopleDetailEntity
import azari.amirhossein.filmora.data.database.entity.PeopleEntity
import azari.amirhossein.filmora.data.database.entity.TvEntity


@Database(
    entities = [HomeEntity::class, MovieEntity::class, MediaDetailEntity::class , PeopleDetailEntity::class, PeopleEntity::class , TvEntity::class],
    version =10,
    exportSchema = false
)
@TypeConverters(ResponseTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ado(): AppDao
}

