package azari.amirhossein.filmora.data.database

import androidx.room.TypeConverter
import azari.amirhossein.filmora.models.ResponseLanguage
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.celebtiry.ResponseTrendingCelebrity
import azari.amirhossein.filmora.models.detail.ResponseAccountStates
import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.models.detail.movie.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.ResponseReviews
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails
import azari.amirhossein.filmora.models.detail.ResponseVideo
import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.models.tv.ResponseTrendingTv
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

    @TypeConverter
    fun fromPopularPeople(value: ResponsePopularCelebrity?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPopularPeople(value: String): ResponsePopularCelebrity? {
        return gson.fromJson(value, ResponsePopularCelebrity::class.java)
    }

    @TypeConverter
    fun fromTrendingPeople(value: ResponseTrendingCelebrity?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTrendingPeople(value: String): ResponseTrendingCelebrity? {
        return gson.fromJson(value, ResponseTrendingCelebrity::class.java)
    }

    @TypeConverter
    fun fromTrendingTv(value: ResponseTrendingTv?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTrendingTv(value: String): ResponseTrendingTv? {
        return gson.fromJson(value, ResponseTrendingTv::class.java)
    }

    @TypeConverter
    fun fromPeopleDetail(value: ResponsePeopleDetails?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPeopleDetail(value: String): ResponsePeopleDetails? {
        return gson.fromJson(value, ResponsePeopleDetails::class.java)
    }

    @TypeConverter
    fun fromAccountStates(value: ResponseAccountStates?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAccountStates(value: String): ResponseAccountStates? {
        return gson.fromJson(value, ResponseAccountStates::class.java)
    }
}
