package azari.amirhossein.filmora.data.database

import androidx.room.TypeConverter
import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import com.google.gson.Gson

class ResponseTypeConverter {
    private val gson = Gson()
    @TypeConverter
    fun fromTrending(value: ResponseTrendingList): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTrending(value: String): ResponseTrendingList {
        return gson.fromJson(value, ResponseTrendingList::class.java)
    }

    @TypeConverter
    fun fromGenres(value: ResponseGenresList): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGenres(value: String): ResponseGenresList {
        return gson.fromJson(value, ResponseGenresList::class.java)
    }

    @TypeConverter
    fun fromMovies(value: ResponseMoviesList): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMovies(value: String): ResponseMoviesList {
        return gson.fromJson(value, ResponseMoviesList::class.java)
    }

    @TypeConverter
    fun fromTvs(value: ResponseTvsList): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTvs(value: String): ResponseTvsList {
        return gson.fromJson(value, ResponseTvsList::class.java)
    }
}