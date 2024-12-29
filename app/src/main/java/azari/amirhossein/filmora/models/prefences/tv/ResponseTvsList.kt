package azari.amirhossein.filmora.models.prefences.tv


import com.google.gson.annotations.SerializedName

data class ResponseTvsList(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result?>?,
    @SerializedName("total_pages")
    val totalPages: Int?, // 7
    @SerializedName("total_results")
    val totalResults: Int? // 139
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /cAmpVRgQL2s55CU6K8SaFcUJPNW.jpg
        @SerializedName("first_air_date")
        val firstAirDate: String?, // 2024-08-20
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int?, // 262252
        @SerializedName("name")
        val name: String?, // KLASS 95: The Power of Beauty
        @SerializedName("origin_country")
        val originCountry: List<String?>?,
        @SerializedName("original_language")
        val originalLanguage: String?, // es
        @SerializedName("original_name")
        val originalName: String?, // Klass 95: El poder de la belleza
        @SerializedName("overview")
        val overview: String?, // An ambitious young woman launches a powerful modeling agency determined to change lives — but power and love are not for the faint of heart.
        @SerializedName("popularity")
        val popularity: Double?, // 26.095
        @SerializedName("poster_path")
        val posterPath: String?, // /dsEtoQtIurztGFs3H9vsa5Tdw2R.jpg
        @SerializedName("vote_average")
        val voteAverage: Double?, // 6.9
        @SerializedName("vote_count")
        val voteCount: Int? // 11
    )
}