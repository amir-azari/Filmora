package azari.amirhossein.filmora.data.source

import azari.amirhossein.filmora.data.database.AppDao
import azari.amirhossein.filmora.data.database.entity.HomeEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dao: AppDao) {
    //Home
    suspend fun saveHomeData(entity: HomeEntity) = dao.saveHomeData(entity)
    fun getHomeData() = dao.getHomeData()

}