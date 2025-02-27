package azari.amirhossein.filmora.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.data.database.entity.MovieAccountStatesEntity
import azari.amirhossein.filmora.data.database.entity.MovieDetailEntity
import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.PeopleDetailEntity
import azari.amirhossein.filmora.data.database.entity.PeopleEntity
import azari.amirhossein.filmora.data.database.entity.TvAccountStatesEntity
import azari.amirhossein.filmora.data.database.entity.TvDetailEntity
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
    @Query("SELECT * FROM ${Constants.Database.MOVIE_DETAIL_TABLE} WHERE id = :id")
    fun getMovieDetailById(id: Int): Flow<MovieDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovieDetail(detail: MovieDetailEntity)

    @Query("DELETE FROM ${Constants.Database.MOVIE_DETAIL_TABLE} WHERE timestamp < :expirationTime")
    suspend fun deleteExpiredMovieDetailData(expirationTime: Long)

    @Query("SELECT * FROM ${Constants.Database.TV_DETAIL_TABLE} WHERE id = :id")
    fun getTvDetailById(id: Int): Flow<TvDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTvDetail(detail: TvDetailEntity)

    @Query("DELETE FROM ${Constants.Database.TV_DETAIL_TABLE} WHERE timestamp < :expirationTime")
    suspend fun deleteExpiredTvDetailData(expirationTime: Long)

    // People Detail
    @Query("SELECT * FROM ${Constants.Database.PEOPLE_DETAIL_TABLE} WHERE id = :id")
    fun getPeopleDetailById(id: Int): Flow<PeopleDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePeopleDetail(detail: PeopleDetailEntity)

    @Query("DELETE FROM ${Constants.Database.PEOPLE_DETAIL_TABLE} WHERE timestamp < :expirationTime")
    suspend fun deleteExpiredPeopleDetailData(expirationTime: Long)

    // Movie Account States
    @Query("SELECT * FROM ${Constants.Database.MOVIE_ACCOUNT_STATES_TABLE} WHERE id = :movieId")
    fun getMovieAccountStates(movieId: Int): Flow<MovieAccountStatesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovieAccountStates(states: MovieAccountStatesEntity)

    @Query("DELETE FROM ${Constants.Database.MOVIE_ACCOUNT_STATES_TABLE} WHERE timestamp < :expirationTime")
    suspend fun deleteExpiredMovieAccountStates(expirationTime: Long)

    // Tv Account States
    @Query("SELECT * FROM ${Constants.Database.TV_ACCOUNT_STATES_TABLE} WHERE id = :tvId")
    fun getTvAccountStates(tvId: Int): Flow<TvAccountStatesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTvAccountStates(states: TvAccountStatesEntity)

    @Query("DELETE FROM ${Constants.Database.TV_ACCOUNT_STATES_TABLE} WHERE timestamp < :expirationTime")
    suspend fun deleteExpiredTvAccountStates(expirationTime: Long)
}
