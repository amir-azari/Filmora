package azari.amirhossein.filmora.data.source

import azari.amirhossein.filmora.data.database.AppDao
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.data.database.entity.MovieAccountStatesEntity
import azari.amirhossein.filmora.data.database.entity.MovieDetailEntity
import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.PeopleDetailEntity
import azari.amirhossein.filmora.data.database.entity.PeopleEntity
import azari.amirhossein.filmora.data.database.entity.TvAccountStatesEntity
import azari.amirhossein.filmora.data.database.entity.TvDetailEntity
import azari.amirhossein.filmora.data.database.entity.TvEntity
import javax.inject.Inject
class LocalDataSource @Inject constructor(private val dao: AppDao) {
    // Home
    suspend fun saveHomeData(entity: HomeEntity) = dao.saveHomeData(entity)
    fun getHomeData() = dao.getHomeData()

    // Movie
    suspend fun saveMovieData(entity: MovieEntity) = dao.saveMovieData(entity)
    fun getMovieData() = dao.getMovieData()

    // TV
    suspend fun saveTvData(entity: TvEntity) = dao.saveTvData(entity)
    fun getTvData() = dao.getTvData()

    // People
    suspend fun savePeopleData(entity: PeopleEntity) = dao.savePeopleData(entity)
    fun getPeopleData() = dao.getPeopleData()

    // Media Detail
    suspend fun saveMovieDetail(entity: MovieDetailEntity) = dao.saveMovieDetail(entity)
    fun getMovieDetailById(id: Int) = dao.getMovieDetailById(id)
    suspend fun deleteExpiredMovieDetailData(expirationTime: Long) = dao.deleteExpiredMovieDetailData(expirationTime)

    suspend fun saveTvDetail(entity: TvDetailEntity) = dao.saveTvDetail(entity)
    fun getTvDetailById(id: Int) = dao.getTvDetailById(id)
    suspend fun deleteExpiredTvDetailData(expirationTime: Long) = dao.deleteExpiredTvDetailData(expirationTime)

    // People Detail
    suspend fun savePeopleDetail(entity: PeopleDetailEntity) = dao.savePeopleDetail(entity)
    fun getPeopleDetailById(id: Int) = dao.getPeopleDetailById(id)
    suspend fun deleteExpiredPeopleDetailData(expirationTime: Long) = dao.deleteExpiredPeopleDetailData(expirationTime)

    // Movie Account States
    suspend fun saveMovieAccountStates(entity: MovieAccountStatesEntity) = dao.saveMovieAccountStates(entity)
    fun getMovieAccountStates(movieId: Int) = dao.getMovieAccountStates(movieId)
    suspend fun deleteExpiredMovieAccountStates(expirationTime: Long) = dao.deleteExpiredMovieAccountStates(expirationTime)

    // Tv Account States
    suspend fun saveTvAccountStates(entity: TvAccountStatesEntity) = dao.saveTvAccountStates(entity)
    fun getTvAccountStates(tvId: Int) = dao.getTvAccountStates(tvId)
    suspend fun deleteExpiredTvAccountStates(expirationTime: Long) = dao.deleteExpiredTvAccountStates(expirationTime)
}