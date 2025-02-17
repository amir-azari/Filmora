package azari.amirhossein.filmora.data.source

import azari.amirhossein.filmora.data.database.AppDao
import azari.amirhossein.filmora.data.database.entity.MediaDetailEntity
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import azari.amirhossein.filmora.data.database.entity.MovieEntity
import azari.amirhossein.filmora.data.database.entity.PeopleDetailEntity
import azari.amirhossein.filmora.data.database.entity.PeopleEntity
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
    suspend fun saveMediaDetail(entity: MediaDetailEntity) = dao.saveMediaDetail(entity)
    fun getMediaDetailById(id: Int) = dao.getMediaDetailById(id)
    suspend fun deleteExpiredMediaDetailData(expirationTime: Long) = dao.deleteExpiredMediaDetailData(expirationTime)

    // People Detail
    suspend fun savePeopleDetail(entity: PeopleDetailEntity) = dao.savePeopleDetail(entity)
    fun getPeopleDetailById(id: Int) = dao.getPeopleDetailById(id)
    suspend fun deleteExpiredPeopleDetailData(expirationTime: Long) = dao.deleteExpiredPeopleDetailData(expirationTime)

}