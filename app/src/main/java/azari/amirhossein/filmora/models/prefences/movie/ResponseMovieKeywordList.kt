package azari.amirhossein.filmora.models.prefences.movie


import com.google.gson.annotations.SerializedName


data class ResponseMovieKeywordList(
    @SerializedName("id")
    val id: Int?, // 121
    @SerializedName("keywords")
    val keywords: List<Keyword?>?
) {
    data class Keyword(
        @SerializedName("id")
        val id: Int?, // 10364
        @SerializedName("name")
        val name: String? // mission
    )
}