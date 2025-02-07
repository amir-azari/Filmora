package azari.amirhossein.filmora.data.source

import azari.amirhossein.filmora.data.database.AppDao
import azari.amirhossein.filmora.data.database.entity.DetailEntity
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.PeopleEntity
import javax.inject.Inject
class LocalDataSource @Inject constructor(private val dao: AppDao) {
    // Home
    suspend fun saveHomeData(entity: HomeEntity) = dao.saveHomeData(entity)
    fun getHomeData() = dao.getHomeData()

    // Movie
    suspend fun saveMovieData(entity: MovieEntity) = dao.saveMovieData(entity)
    fun getMovieData() = dao.getMovieData()

    // People
    suspend fun savePeopleData(entity: PeopleEntity) = dao.savePeopleData(entity)
    fun getPeopleData() = dao.getPeopleData()
    // Detail
    suspend fun saveDetail(entity: DetailEntity) = dao.saveDetail(entity)
    fun getDetailById(id: Int) = dao.getDetailById(id)
    suspend fun deleteExpiredDetailData(expirationTime: Long) = dao.deleteExpiredDetailData(expirationTime)
}