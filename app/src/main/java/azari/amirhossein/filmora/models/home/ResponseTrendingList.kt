package azari.amirhossein.filmora.models.home


import com.google.gson.annotations.SerializedName

data class ResponseTrendingList(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    var results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int?, // 500
    @SerializedName("total_results")
    val totalResults: Int? // 10000
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /2meX1nMdScFOoV4370rqHWKmXhY.jpg
        @SerializedName("first_air_date")
        val firstAirDate: String?, // 2021-09-17
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int, // 93405
        @SerializedName("media_type")
        val mediaType: String, // tv
        @SerializedName("name")
        val name: String?, // Squid Game
        @SerializedName("origin_country")
        val originCountry: List<String?>?,
        @SerializedName("original_language")
        val originalLanguage: String?, // ko
        @SerializedName("original_name")
        val originalName: String?, // 오징어 게임
        @SerializedName("original_title")
        val originalTitle: String?, // Wicked
        @SerializedName("overview")
        val overview: String?, // Hundreds of cash-strapped players accept a strange invitation to compete in children's games. Inside, a tempting prize awaits — with deadly high stakes.
        @SerializedName("popularity")
        val popularity: Double?, // 4393.859
        @SerializedName("poster_path")
        val posterPath: String?, // /dDlEmu3EZ0Pgg93K2SVNLCjCSvE.jpg
        @SerializedName("release_date")
        val releaseDate: String?, // 2024-11-20
        @SerializedName("title")
        val title: String?, // Wicked
        @SerializedName("video")
        val video: Boolean?, // false
        @SerializedName("vote_average")
        val voteAverage: Double?, // 7.8
        @SerializedName("vote_count")
        val voteCount: Int? // 14380
    )
}