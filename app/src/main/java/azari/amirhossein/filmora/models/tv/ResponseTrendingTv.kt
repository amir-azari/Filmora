package azari.amirhossein.filmora.models.tv


import com.google.gson.annotations.SerializedName

data class ResponseTrendingTv(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result?>?,
    @SerializedName("total_pages")
    val totalPages: Int?, // 500
    @SerializedName("total_results")
    val totalResults: Int? // 10000
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /fOetFgvvZxMgH2TC0ULlIrpgosH.jpg
        @SerializedName("first_air_date")
        val firstAirDate: String?, // 2025-02-07
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int?, // 233742
        @SerializedName("media_type")
        val mediaType: String?, // tv
        @SerializedName("name")
        val name: String?, // Newtopia
        @SerializedName("origin_country")
        val originCountry: List<String?>?,
        @SerializedName("original_language")
        val originalLanguage: String?, // ko
        @SerializedName("original_name")
        val originalName: String?, // 뉴토피아
        @SerializedName("overview")
        val overview: String?, // Jae-yoon, a late military enlistee, and his girlfriend, Young-joo, break up over the phone over growing misunderstandings. But a zombie outbreak rocks the world. A national emergency is declared, a plane crashes in the city center, and Jae-yoon and his unit get trapped on top of a Seoul skyscraper. Young-joo risks the zombie-filled streets to find him. Can their love survive the apocalypse?
        @SerializedName("popularity")
        val popularity: Double?, // 226.002
        @SerializedName("poster_path")
        val posterPath: String?, // /hP9bsI2B7HONwDrzoNw5Q3QJMqU.jpg
        @SerializedName("vote_average")
        val voteAverage: Double?, // 9.9
        @SerializedName("vote_count")
        val voteCount: Int? // 28
    )
}