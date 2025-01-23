package azari.amirhossein.filmora.models.detail


import com.google.gson.annotations.SerializedName

data class ResponseTvSimilar(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result?>?,
    @SerializedName("total_pages")
    val totalPages: Int?, // 2228
    @SerializedName("total_results")
    val totalResults: Int? // 44543
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /k8PNGEHsG09iDvh1xgPRAH2CHiO.jpg
        @SerializedName("first_air_date")
        val firstAirDate: String?, // 2016-10-13
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int, // 86354
        @SerializedName("name")
        val name: String?, // Sons of the Caliphate
        @SerializedName("origin_country")
        val originCountry: List<String?>?,
        @SerializedName("original_language")
        val originalLanguage: String?, // en
        @SerializedName("original_name")
        val originalName: String?, // Sons of the Caliphate
        @SerializedName("overview")
        val overview: String?, // This is a drama series about the lives of three rich, entitled, passionate and ambitious young men: Kalifah, Nuhu and Diko. Elections are fast approaching and there's a tussle for the governorship seat of the Northern Caliphate State of Kowa. Soon personal conflicts intertwine between their families in a race to crown the next Governor.
        @SerializedName("popularity")
        val popularity: Double?, // 5.429
        @SerializedName("poster_path")
        val posterPath: String?, // /lRezbhAYQx40CFZ7JRbTwWu8d9M.jpg
        @SerializedName("vote_average")
        val voteAverage: Double?, // 8.563
        @SerializedName("vote_count")
        val voteCount: Int? // 2
    )
}