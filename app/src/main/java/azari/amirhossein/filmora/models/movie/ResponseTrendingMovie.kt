package azari.amirhossein.filmora.models.movie


import com.google.gson.annotations.SerializedName

data class ResponseTrendingMovie(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int?, // 500
    @SerializedName("total_results")
    val totalResults: Int? // 10000
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /zo8CIjJ2nfNOevqNajwMRO6Hwka.jpg
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int, // 1241982
        @SerializedName("media_type")
        val mediaType: String?, // movie
        @SerializedName("original_language")
        val originalLanguage: String?, // en
        @SerializedName("original_title")
        val originalTitle: String?, // Moana 2
        @SerializedName("overview")
        val overview: String?, // After receiving an unexpected call from her wayfinding ancestors, Moana journeys alongside Maui and a new crew to the far seas of Oceania and into dangerous, long-lost waters for an adventure unlike anything she's ever faced.
        @SerializedName("popularity")
        val popularity: Double?, // 2932.481
        @SerializedName("poster_path")
        val posterPath: String?, // /aLVkiINlIeCkcZIzb7XHzPYgO6L.jpg
        @SerializedName("release_date")
        val releaseDate: String?, // 2024-11-21
        @SerializedName("title")
        val title: String?, // Moana 2
        @SerializedName("video")
        val video: Boolean?, // false
        @SerializedName("vote_average")
        val voteAverage: Double?, // 7.2
        @SerializedName("vote_count")
        val voteCount: Int? // 1216
    )
}