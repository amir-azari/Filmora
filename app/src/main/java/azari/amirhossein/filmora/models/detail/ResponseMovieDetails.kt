package azari.amirhossein.filmora.models.detail

import android.os.Parcelable
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResponseMovieDetails(
    @SerializedName("adult")
    val adult: Boolean?, // false
    @SerializedName("backdrop_path")
    val backdropPath: String?, // /hZkgoQYus5vegHoetLkCJzb17zJ.jpg
    @SerializedName("belongs_to_collection")
    val belongsToCollection: BelongsToCollection?, // null
    @SerializedName("budget")
    val budget: Int?, // 63000000
    @SerializedName("credits")
    val credits: ResponseCredit,
    @SerializedName("genres")
    val genres: List<ResponseGenresList.Genre?>?,
    @SerializedName("homepage")
    val homepage: String?, // http://www.foxmovies.com/movies/fight-club
    @SerializedName("id")
    val id: Int, // 550
    @SerializedName("images")
    val images: ResponseImage,
    @SerializedName("imdb_id")
    val imdbId: String?, // tt0137523
    @SerializedName("origin_country")
    val originCountry: List<String?>?,
    @SerializedName("original_language")
    val originalLanguage: String?, // en
    @SerializedName("original_title")
    val originalTitle: String?, // Fight Club
    @SerializedName("overview")
    val overview: String?, // A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground "fight clubs" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.
    @SerializedName("popularity")
    val popularity: Double?, // 26.476
    @SerializedName("poster_path")
    val posterPath: String?, // /pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany?>?,
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry?>?,
    @SerializedName("recommendations")
    val recommendations: Recommendations?,
    @SerializedName("release_date")
    val releaseDate: String?, // 1999-10-15
    @SerializedName("revenue")
    val revenue: Int?, // 100853753
    @SerializedName("reviews")
    val reviews: ResponseReviews,
    @SerializedName("runtime")
    val runtime: Int?, // 139
    @SerializedName("similar")
    val similar: Similar?,
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage?>?,
    @SerializedName("status")
    val status: String?, // Released
    @SerializedName("tagline")
    val tagline: String?, // Mischief. Mayhem. Soap.
    @SerializedName("title")
    val title: String?, // Fight Club
    @SerializedName("video")
    val video: Boolean?, // false
    @SerializedName("videos")
    val videos: ResponseVideo,
    @SerializedName("vote_average")
    val voteAverage: Double?, // 8.438
    @SerializedName("vote_count")
    val voteCount: Int?, // 29871
) {

    data class BelongsToCollection(
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /l5CIAdxVhhaUD3DaS4lP4AR2so9.jpg
        @SerializedName("id")
        val id: Int, // 720879
        @SerializedName("name")
        val name: String?, // Sonic the Hedgehog Collection
        @SerializedName("poster_path")
        val posterPath: String?, // /fwFWhYXj8wY6gFACtecJbg229FI.jpg
    )


    data class Recommendations(
        @SerializedName("page")
        val page: Int?, // 1
        @SerializedName("results")
        val results: List<Result?>?,
        @SerializedName("total_pages")
        val totalPages: Int?, // 2
        @SerializedName("total_results")
        val totalResults: Int?, // 40
    )  {
        data class Result(
            @SerializedName("adult")
            val adult: Boolean?, // false
            @SerializedName("backdrop_path")
            val backdropPath: String?, // /suaEOtk1N1sgg2MTM7oZd2cfVp3.jpg
            @SerializedName("genre_ids")
            val genreIds: List<Int?>?,
            @SerializedName("id")
            val id: Int, // 680
            @SerializedName("media_type")
            val mediaType: String?, // movie
            @SerializedName("original_language")
            val originalLanguage: String?, // en
            @SerializedName("original_title")
            val originalTitle: String?, // Pulp Fiction
            @SerializedName("overview")
            val overview: String?, // A burger-loving hit man, his philosophical partner, a drug-addled gangster's moll and a washed-up boxer converge in this sprawling, comedic crime caper. Their adventures unfurl in three stories that ingeniously trip back and forth in time.
            @SerializedName("popularity")
            val popularity: Double?, // 23.429
            @SerializedName("poster_path")
            val posterPath: String?, // /jYqKxBbAUdfKq3BfHKivJytFiPL.jpg
            @SerializedName("release_date")
            val releaseDate: String?, // 1994-09-10
            @SerializedName("title")
            val title: String?, // Pulp Fiction
            @SerializedName("video")
            val video: Boolean?, // false
            @SerializedName("vote_average")
            val voteAverage: Double?, // 8.489
            @SerializedName("vote_count")
            val voteCount: Int?, // 28349
        )
    }
    data class Similar(
        @SerializedName("page")
        val page: Int?, // 1
        @SerializedName("results")
        val results: List<Result?>?,
        @SerializedName("total_pages")
        val totalPages: Int?, // 12566
        @SerializedName("total_results")
        val totalResults: Int?, // 251317
    ) {
        @Parcelize
        data class Result(
            @SerializedName("adult")
            val adult: Boolean?, // false
            @SerializedName("backdrop_path")
            val backdropPath: String?, // /uSwWQLDaHuROPaX5N1xpOdkZW5K.jpg
            @SerializedName("genre_ids")
            val genreIds: List<Int?>?,
            @SerializedName("id")
            val id: Int, // 26974
            @SerializedName("original_language")
            val originalLanguage: String?, // ko
            @SerializedName("original_title")
            val originalTitle: String?, // 불가사리
            @SerializedName("overview")
            val overview: String?, // In feudal Korea, a group of starving villagers grow weary of the orders handed down to them by their controlling king and set out to use a deadly monster under their control to push his armies back.
            @SerializedName("popularity")
            val popularity: Double?, // 2.006
            @SerializedName("poster_path")
            val posterPath: String?, // /dpScmSrkTqvirlWkNgUsnTVxe40.jpg
            @SerializedName("release_date")
            val releaseDate: String?, // 1985-12-01
            @SerializedName("title")
            val title: String?, // Pulgasari
            @SerializedName("video")
            val video: Boolean?, // false
            @SerializedName("vote_average")
            val voteAverage: Double?, // 5.6
            @SerializedName("vote_count")
            val voteCount: Int?, // 50
        ) : Parcelable
    }

}