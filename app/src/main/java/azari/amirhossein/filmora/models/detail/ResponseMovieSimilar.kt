package azari.amirhossein.filmora.models.detail


import com.google.gson.annotations.SerializedName

data class ResponseMovieSimilar(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result?>?,
    @SerializedName("total_pages")
    val totalPages: Int?, // 15699
    @SerializedName("total_results")
    val totalResults: Int? // 313962
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /zM3ifcl5G5uqw7aLBkyrDFLJfs4.jpg
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int, // 927602
        @SerializedName("original_language")
        val originalLanguage: String?, // ta
        @SerializedName("original_title")
        val originalTitle: String?, // வீட்ல விசேஷம்
        @SerializedName("overview")
        val overview: String?, // While Elango struggles to accept the news of his middle-aged mother's pregnancy, the situation jeopardises his relationship with his girlfriend, Sowmya, and his parents face social ridicule.
        @SerializedName("popularity")
        val popularity: Double?, // 2.125
        @SerializedName("poster_path")
        val posterPath: String?, // /ulg3XRanMIZpVtDHIk4K4zCWxWz.jpg
        @SerializedName("release_date")
        val releaseDate: String?, // 2022-06-17
        @SerializedName("title")
        val title: String?, // Veetla Vishesham
        @SerializedName("video")
        val video: Boolean?, // false
        @SerializedName("vote_average")
        val voteAverage: Double?, // 6.3
        @SerializedName("vote_count")
        val voteCount: Int? // 6
    )
}