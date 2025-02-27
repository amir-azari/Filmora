package azari.amirhossein.filmora.models.detail


import com.google.gson.annotations.SerializedName

data class ResponseAccountStates(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("favorite")
    val favorite: Boolean = true,
    @SerializedName("rated")
    val rated: Rated? = null,
    @SerializedName("watchlist")
    val watchlist: Boolean = true
) {
    data class Rated(
        @SerializedName("value")
        val value: Int = 0
    )
}
