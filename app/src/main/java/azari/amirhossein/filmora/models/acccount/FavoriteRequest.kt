package azari.amirhossein.filmora.models.acccount


import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @SerializedName("favorite")
    val favorite: Boolean, // true
    @SerializedName("media_id")
    val mediaId: Int, // 550
    @SerializedName("media_type")
    val mediaType: String // movie
)