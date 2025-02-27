package azari.amirhossein.filmora.models.acccount


import com.google.gson.annotations.SerializedName


data class WatchlistRequest(
    @SerializedName("media_id")
    val mediaId: Int, // 550
    @SerializedName("media_type")
    val mediaType: String, // movie
    @SerializedName("watchlist")
    val watchlist: Boolean? // true
)