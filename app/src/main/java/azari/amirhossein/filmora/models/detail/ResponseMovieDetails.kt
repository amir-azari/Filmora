package azari.amirhossein.filmora.models.detail

import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import com.google.gson.annotations.SerializedName

data class ResponseMovieDetails(
    @SerializedName("adult")
    val adult: Boolean?, // false
    @SerializedName("backdrop_path")
    val backdropPath: String?, // /zOpe0eHsq0A2NvNyBbtT6sj53qV.jpg
    @SerializedName("belongs_to_collection")
    val belongsToCollection: BelongsToCollection?,
    @SerializedName("budget")
    val budget: Int?, // 122000000
    @SerializedName("genres")
    val genres: List<ResponseGenresList.Genre?>?,
    @SerializedName("homepage")
    val homepage: String?, // https://www.sonicthehedgehogmovie.com
    @SerializedName("id")
    val id: Int?, // 939243
    @SerializedName("imdb_id")
    val imdbId: String?, // tt18259086
    @SerializedName("origin_country")
    val originCountry: List<String?>?,
    @SerializedName("original_language")
    val originalLanguage: String?, // en
    @SerializedName("original_title")
    val originalTitle: String?, // Sonic the Hedgehog 3
    @SerializedName("overview")
    val overview: String?, // Sonic, Knuckles, and Tails reunite against a powerful new adversary, Shadow, a mysterious villain with powers unlike anything they have faced before. With their abilities outmatched in every way, Team Sonic must seek out an unlikely alliance in hopes of stopping Shadow and protecting the planet.
    @SerializedName("popularity")
    val popularity: Double?, // 2732.833
    @SerializedName("poster_path")
    val posterPath: String?, // /d8Ryb8AunYAuycVKDp5HpdWPKgC.jpg
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany?>?,
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry?>?,
    @SerializedName("release_date")
    val releaseDate: String?, // 2024-12-19
    @SerializedName("revenue")
    val revenue: Int?, // 384815196
    @SerializedName("runtime")
    val runtime: Int?, // 110
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage?>?,
    @SerializedName("status")
    val status: String?, // Released
    @SerializedName("tagline")
    val tagline: String?, // New adventure. New rival.
    @SerializedName("title")
    val title: String?, // Sonic the Hedgehog 3
    @SerializedName("video")
    val video: Boolean?, // false
    @SerializedName("vote_average")
    val voteAverage: Double?, // 7.604
    @SerializedName("vote_count")
    val voteCount: Int? // 551
) {
    data class BelongsToCollection(
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /l5CIAdxVhhaUD3DaS4lP4AR2so9.jpg
        @SerializedName("id")
        val id: Int?, // 720879
        @SerializedName("name")
        val name: String?, // Sonic the Hedgehog Collection
        @SerializedName("poster_path")
        val posterPath: String? // /fwFWhYXj8wY6gFACtecJbg229FI.jpg
    )

    data class ProductionCompany(
        @SerializedName("id")
        val id: Int?, // 4
        @SerializedName("logo_path")
        val logoPath: String?, // /gz66EfNoYPqHTYI4q9UEN4CbHRc.png
        @SerializedName("name")
        val name: String?, // Paramount Pictures
        @SerializedName("origin_country")
        val originCountry: String? // US
    )

    data class ProductionCountry(
        @SerializedName("iso_3166_1")
        val iso31661: String?, // JP
        @SerializedName("name")
        val name: String? // Japan
    )

    data class SpokenLanguage(
        @SerializedName("english_name")
        val englishName: String?, // English
        @SerializedName("iso_639_1")
        val iso6391: String?, // en
        @SerializedName("name")
        val name: String? // English
    )
}