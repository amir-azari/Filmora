package azari.amirhossein.filmora.data.models

import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("genres")
    val genres: List<Genre> = emptyList(),
) {
    data class Genre(
        @SerializedName("id")
        val id: Int?, // 28
        @SerializedName("name")
        val name: String? // Action
    )
}