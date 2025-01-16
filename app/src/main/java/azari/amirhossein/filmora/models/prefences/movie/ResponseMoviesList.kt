package azari.amirhossein.filmora.models.prefences.movie


import com.google.gson.annotations.SerializedName

data class ResponseMoviesList(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int?, // 36
    @SerializedName("total_results")
    val totalResults: Int? // 712
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /jMbSOKHbzIK1nCZsvVamgTbVsCY.jpg
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int, // 1356991
        @SerializedName("original_language")
        val originalLanguage: String?, // xx
        @SerializedName("original_title")
        val originalTitle: String?, // Lord
        @SerializedName("overview")
        val overview: String?, // Moving and changing worlds. They start with eight images and merge, gradually diminishing. In the end, they intertwine and become one.
        @SerializedName("popularity")
        val popularity: Double?, // 0.431
        @SerializedName("poster_path")
        val posterPath: String?, // /nTUYqnPrTr1CccWjGMVTLdduYm.jpg
        @SerializedName("release_date")
        val releaseDate: String?, // 2024-10-26
        @SerializedName("title")
        val title: String?, // Lord
        @SerializedName("video")
        val video: Boolean?, // false
        @SerializedName("vote_average")
        val voteAverage: Double?, // 7.3
        @SerializedName("vote_count")
        val voteCount: Int? // 0
    )
}