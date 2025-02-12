package azari.amirhossein.filmora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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
import azari.amirhossein.filmora.utils.Constants

@Entity(tableName = Constants.Database.MEDIA_DETAIL_TABLE)
data class MediaDetailEntity(
    @PrimaryKey val id: Int = 0,
    val movie: ResponseMovieDetails? = null,
    val tv: ResponseTvDetails? = null,
    val credits: ResponseCredit? = null,
    val language: ResponseLanguage? = null,
    val similar: ResponseMovieSimilar? = null,
    val recommendations: ResponseMovieRecommendations? = null,
    val tvSimilar: ResponseTvSimilar? = null,
    val tvRecommendations: ResponseTvRecommendations? = null,
    val videos: ResponseVideo? = null,
    val images: ResponseImage? = null,
    val reviews: ResponseReviews? =null,
    val timestamp: Long = System.currentTimeMillis()

)