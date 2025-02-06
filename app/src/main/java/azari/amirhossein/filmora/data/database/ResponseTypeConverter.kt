package azari.amirhossein.filmora.data.database

import androidx.room.TypeConverter
import azari.amirhossein.filmora.models.ResponseLanguage
import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.models.detail.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.ResponseMovieRecommendations
import azari.amirhossein.filmora.models.detail.ResponseMovieSimilar
import azari.amirhossein.filmora.models.detail.ResponseReviews
import azari.amirhossein.filmora.models.detail.ResponseTvDetails
import azari.amirhossein.filmora.models.detail.ResponseTvRecommendations
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.models.detail.ResponseVideo
import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie
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

    @TypeConverter
    fun fromMovieDetails(value: ResponseMovieDetails?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMovieDetails(value: String): ResponseMovieDetails? {
        return gson.fromJson(value, ResponseMovieDetails::class.java)
    }

    @TypeConverter
    fun fromTvDetails(value: ResponseTvDetails?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTvDetails(value: String): ResponseTvDetails? {
        return gson.fromJson(value, ResponseTvDetails::class.java)
    }

    @TypeConverter
    fun fromCredits(value: ResponseCredit?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCredits(value: String): ResponseCredit? {
        return gson.fromJson(value, ResponseCredit::class.java)
    }

    @TypeConverter
    fun fromLanguage(value: ResponseLanguage?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLanguage(value: String): ResponseLanguage? {
        return gson.fromJson(value, ResponseLanguage::class.java)
    }

    @TypeConverter
    fun fromMovieSimilar(value: ResponseMovieSimilar?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMovieSimilar(value: String): ResponseMovieSimilar? {
        return gson.fromJson(value, ResponseMovieSimilar::class.java)
    }

    @TypeConverter
    fun fromMovieRecommendations(value: ResponseMovieRecommendations?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMovieRecommendations(value: String): ResponseMovieRecommendations? {
        return gson.fromJson(value, ResponseMovieRecommendations::class.java)
    }

    @TypeConverter
    fun fromTvSimilar(value: ResponseTvSimilar?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTvSimilar(value: String): ResponseTvSimilar? {
        return gson.fromJson(value, ResponseTvSimilar::class.java)
    }

    @TypeConverter
    fun fromTvRecommendations(value: ResponseTvRecommendations?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTvRecommendations(value: String): ResponseTvRecommendations? {
        return gson.fromJson(value, ResponseTvRecommendations::class.java)
    }

    @TypeConverter
    fun fromVideos(value: ResponseVideo?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toVideos(value: String): ResponseVideo? {
        return gson.fromJson(value, ResponseVideo::class.java)
    }

    @TypeConverter
    fun fromImages(value: ResponseImage?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toImages(value: String): ResponseImage? {
        return gson.fromJson(value, ResponseImage::class.java)
    }

    @TypeConverter
    fun fromReviews(value: ResponseReviews?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toReviews(value: String): ResponseReviews? {
        return gson.fromJson(value, ResponseReviews::class.java)
    }

    @TypeConverter
    fun fromTrendingMovie(value: ResponseTrendingMovie?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTrendingMovie(value: String): ResponseTrendingMovie? {
        return gson.fromJson(value, ResponseTrendingMovie::class.java)
    }

}
