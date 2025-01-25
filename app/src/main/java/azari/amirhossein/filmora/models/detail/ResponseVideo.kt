package azari.amirhossein.filmora.models.detail


import com.google.gson.annotations.SerializedName

data class ResponseVideo(
    @SerializedName("id")
    val id: Int?, // 839033
    @SerializedName("results")
    val results: List<Result>?
) {
    data class Result(
        @SerializedName("id")
        val id: String?, // 677d526b5c738e1c22669d99
        @SerializedName("iso_3166_1")
        val iso31661: String?, // US
        @SerializedName("iso_639_1")
        val iso6391: String?, // en
        @SerializedName("key")
        val key: String?, // Pghe-u1QECY
        @SerializedName("name")
        val name: String?, // BTS: Return to Helm's Deep
        @SerializedName("official")
        val official: Boolean?, // true
        @SerializedName("published_at")
        val publishedAt: String?, // 2025-01-07T15:43:11.000Z
        @SerializedName("site")
        val site: String?, // YouTube
        @SerializedName("size")
        val size: Int?, // 1080
        @SerializedName("type")
        val type: String? // Behind the Scenes
    )
}