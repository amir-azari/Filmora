package azari.amirhossein.filmora.models.prefences.tv


import com.google.gson.annotations.SerializedName

data class ResponseTvKeywordList(
    @SerializedName("id")
    val id: Int?, // 93405
    @SerializedName("results")
    val results: List<Result?>?,
) {
    data class Result(
        @SerializedName("id")
        val id: Int?, // 3271
        @SerializedName("name")
        val name: String?, // secret organization
    )
}