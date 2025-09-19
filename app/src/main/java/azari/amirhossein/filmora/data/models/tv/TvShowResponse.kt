package azari.amirhossein.filmora.data.models.tv

import com.google.gson.annotations.SerializedName

data class TvShowResponse(
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("results")
    val results: List<Result> = emptyList(),
    @SerializedName("total_pages")
    val totalPages: Int = 0,
    @SerializedName("total_results")
    val totalResults: Int = 0
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean = false,
        @SerializedName("backdrop_path")
        val backdropPath: String? = null,
        @SerializedName("first_air_date")
        val firstAirDate: String? = null,
        @SerializedName("genre_ids")
        val genreIds: List<Int> = emptyList(),
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("name")
        val name: String = "",
        @SerializedName("origin_country")
        val originCountry: List<String> = emptyList(),
        @SerializedName("original_language")
        val originalLanguage: String = "",
        @SerializedName("original_name")
        val originalName: String = "",
        @SerializedName("overview")
        val overview: String = "",
        @SerializedName("popularity")
        val popularity: Double = 0.0,
        @SerializedName("poster_path")
        val posterPath: String? = null,
        @SerializedName("vote_average")
        val voteAverage: Double = 0.0,
        @SerializedName("vote_count")
        val voteCount: Int = 0
    )
}
