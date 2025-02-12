package azari.amirhossein.filmora.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import azari.amirhossein.filmora.data.database.entity.MediaDetailEntity
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.PeopleDetailEntity
import azari.amirhossein.filmora.data.database.entity.PeopleEntity
import azari.amirhossein.filmora.data.database.entity.TvEntity
import azari.amirhossein.filmora.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Home
    @Query("SELECT * FROM ${Constants.Database.HOME_TABLE} WHERE id = 0 LIMIT 1")
    fun getHomeData(): Flow<HomeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHomeData(data: HomeEntity)

    // Movie
    @Query("SELECT * FROM ${Constants.Database.MOVIES_TABLE} WHERE id = 0 LIMIT 1")
    fun getMovieData(): Flow<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovieData(data: MovieEntity)

    // TV
    @Query("SELECT * FROM ${Constants.Database.TV_SHOW_TABLE} WHERE id = 0 LIMIT 1")
    fun getTvData(): Flow<TvEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTvData(data: TvEntity)

    // People
    @Query("SELECT * FROM ${Constants.Database.CELEBRITIES_TABLE} WHERE id = 0 LIMIT 1")
    fun getPeopleData(): Flow<PeopleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePeopleData(data: PeopleEntity)

    // Media Detail
    @Query("SELECT * FROM ${Constants.Database.MEDIA_DETAIL_TABLE} WHERE id = :id")
    fun getMediaDetailById(id: Int): Flow<MediaDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMediaDetail(detail: MediaDetailEntity)

    @Query("DELETE FROM ${Constants.Database.MEDIA_DETAIL_TABLE} WHERE timestamp < :expirationTime")
    suspend fun deleteExpiredMediaDetailData(expirationTime: Long)

    // People Detail
    @Query("SELECT * FROM ${Constants.Database.PEOPLE_DETAIL_TABLE} WHERE id = :id")
    fun getPeopleDetailById(id: Int): Flow<PeopleDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePeopleDetail(detail: PeopleDetailEntity)

    @Query("DELETE FROM ${Constants.Database.PEOPLE_DETAIL_TABLE} WHERE timestamp < :expirationTime")
    suspend fun deleteExpiredPeopleDetailData(expirationTime: Long)

}
