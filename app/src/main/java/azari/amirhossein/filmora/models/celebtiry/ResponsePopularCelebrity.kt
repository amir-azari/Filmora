package azari.amirhossein.filmora.models.celebtiry


import com.google.gson.annotations.SerializedName


data class ResponsePopularCelebrity(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int?, // 193153
    @SerializedName("total_results")
    val totalResults: Int? // 3863053
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("gender")
        val gender: Int?, // 1
        @SerializedName("id")
        val id: Int, // 122822
        @SerializedName("known_for")
        val knownFor: List<KnownFor?>?,
        @SerializedName("known_for_department")
        val knownForDepartment: String?, // Acting
        @SerializedName("name")
        val name: String?, // Yvonne Yung Hung
        @SerializedName("original_name")
        val originalName: String?, // 翁虹
        @SerializedName("popularity")
        val popularity: Double?, // 223.658
        @SerializedName("profile_path")
        val profilePath: String? // /d7tO5Z1UdpMfutCdW2zfLfXCOfO.jpg
    ) {
        data class KnownFor(
            @SerializedName("adult")
            val adult: Boolean?, // false
            @SerializedName("backdrop_path")
            val backdropPath: String?, // /nPx3FS9ZnQ6fwy9BtgcnNZJpyXC.jpg
            @SerializedName("first_air_date")
            val firstAirDate: String?, // 2022-12-16
            @SerializedName("genre_ids")
            val genreIds: List<Int?>?,
            @SerializedName("id")
            val id: Int?, // 12207
            @SerializedName("media_type")
            val mediaType: String?, // movie
            @SerializedName("name")
            val name: String?, // The Recruit
            @SerializedName("origin_country")
            val originCountry: List<String?>?,
            @SerializedName("original_language")
            val originalLanguage: String?, // cn
            @SerializedName("original_name")
            val originalName: String?, // The Recruit
            @SerializedName("original_title")
            val originalTitle: String?, // 醉拳二
            @SerializedName("overview")
            val overview: String?, // Returning home with his father after a shopping expedition, Wong Fei-Hong is unwittingly caught up in the battle between foreigners who wish to export ancient Chinese artifacts and loyalists who don't want the pieces to leave the country. Fei-Hong must fight against the foreigners using his Drunken Boxing style, and overcome his father's antagonism as well.
            @SerializedName("popularity")
            val popularity: Double?, // 27.815
            @SerializedName("poster_path")
            val posterPath: String?, // /xqUBrSBtPYLvCtfqHF5sapU6Div.jpg
            @SerializedName("release_date")
            val releaseDate: String?, // 1994-02-03
            @SerializedName("title")
            val title: String?, // The Legend of Drunken Master
            @SerializedName("video")
            val video: Boolean?, // false
            @SerializedName("vote_average")
            val voteAverage: Double?, // 7.406
            @SerializedName("vote_count")
            val voteCount: Int? // 971
        )
    }
}