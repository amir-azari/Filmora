package azari.amirhossein.filmora.models.prefences


import com.google.gson.annotations.SerializedName

data class ResponseGenresList(
    @SerializedName("genres")
    val genres: List<Genre?>?
) {
    data class Genre(
        @SerializedName("id")
        val id: Int?, // 28
        @SerializedName("name")
        val name: String? // Action
    )
}