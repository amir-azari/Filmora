package azari.amirhossein.filmora.models.detail


import com.google.gson.annotations.SerializedName

data class ResponseTvRecommendations(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result?>?,
    @SerializedName("total_pages")
    val totalPages: Int?, // 2
    @SerializedName("total_results")
    val totalResults: Int? // 40
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /q2dljJjmUBfpI0z5VC17z9S3rSD.jpg
        @SerializedName("first_air_date")
        val firstAirDate: String?, // 2023-07-18
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int, // 201304
        @SerializedName("media_type")
        val mediaType: String?, // tv
        @SerializedName("name")
        val name: String?, // Justified: City Primeval
        @SerializedName("origin_country")
        val originCountry: List<String?>?,
        @SerializedName("original_language")
        val originalLanguage: String?, // en
        @SerializedName("original_name")
        val originalName: String?, // Justified: City Primeval
        @SerializedName("overview")
        val overview: String?, // Having left the hollers of Kentucky 15 years ago, Raylan Givens is now based in Miami, balancing life as a marshal and part-time father of a 15-year-old girl. A chance encounter on a Florida highway sends him to Detroit and he crosses paths with Clement Mansell, aka The Oklahoma Wildman, a violent sociopath who’s already slipped through the fingers of Detroit’s finest once and wants to do so again.
        @SerializedName("popularity")
        val popularity: Double?, // 27.044
        @SerializedName("poster_path")
        val posterPath: String?, // /uTp9cMxFMCk5b7alKb2m5wHaGxb.jpg
        @SerializedName("vote_average")
        val voteAverage: Double?, // 6.888
        @SerializedName("vote_count")
        val voteCount: Int? // 120
    )
}