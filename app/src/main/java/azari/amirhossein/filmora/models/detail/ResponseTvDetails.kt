package azari.amirhossein.filmora.models.detail

import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import com.google.gson.annotations.SerializedName

data class ResponseTvDetails(
    @SerializedName("adult")
    val adult: Boolean?, // false
    @SerializedName("backdrop_path")
    val backdropPath: String?, // /6E0hbesJfpekAqL2AeHYukkHcbD.jpg
    @SerializedName("created_by")
    val createdBy: List<CreatedBy?>?,
    @SerializedName("episode_run_time")
    val episodeRunTime: List<Any?>?,
    @SerializedName("first_air_date")
    val firstAirDate: String?, // 2024-12-02
    @SerializedName("genres")
    val genres: List<ResponseGenresList.Genre?>?,
    @SerializedName("homepage")
    val homepage: String?, // https://www.disneyplus.com/series/star-wars-skeleton-crew/5V2Mi4qOaO77
    @SerializedName("id")
    val id: Int?, // 202879
    @SerializedName("in_production")
    val inProduction: Boolean?, // false
    @SerializedName("languages")
    val languages: List<String?>?,
    @SerializedName("last_air_date")
    val lastAirDate: String?, // 2025-01-14
    @SerializedName("last_episode_to_air")
    val lastEpisodeToAir: LastEpisodeToAir?,
    @SerializedName("name")
    val name: String?, // Star Wars: Skeleton Crew
    @SerializedName("networks")
    val networks: List<Network?>?,
    @SerializedName("next_episode_to_air")
    val nextEpisodeToAir: Any?, // null
    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int?, // 8
    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int?, // 1
    @SerializedName("origin_country")
    val originCountry: List<String?>?,
    @SerializedName("original_language")
    val originalLanguage: String?, // en
    @SerializedName("original_name")
    val originalName: String?, // Star Wars: Skeleton Crew
    @SerializedName("overview")
    val overview: String?, // Four ordinary kids search for their home planet after getting lost in the Star Wars galaxy.
    @SerializedName("popularity")
    val popularity: Double?, // 523.354
    @SerializedName("poster_path")
    val posterPath: String?, // /srQbJhLRKoAwRrNN5ga7webPHbC.jpg
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany?>?,
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry?>?,
    @SerializedName("seasons")
    val seasons: List<Season?>?,
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage?>?,
    @SerializedName("status")
    val status: String?, // Ended
    @SerializedName("tagline")
    val tagline: String?, // A Star Wars adventure.
    @SerializedName("type")
    val type: String?, // Scripted
    @SerializedName("vote_average")
    val voteAverage: Double?, // 7.2
    @SerializedName("vote_count")
    val voteCount: Int? // 144
) {
    data class CreatedBy(
        @SerializedName("credit_id")
        val creditId: String?, // 662bb340c1e56e011df1ccba
        @SerializedName("gender")
        val gender: Int?, // 2
        @SerializedName("id")
        val id: Int?, // 1293994
        @SerializedName("name")
        val name: String?, // Jon Watts
        @SerializedName("original_name")
        val originalName: String?, // Jon Watts
        @SerializedName("profile_path")
        val profilePath: String? // /fkXChMX6CUXY1yOxBehAzvaTCl7.jpg
    )

    data class LastEpisodeToAir(
        @SerializedName("air_date")
        val airDate: String?, // 2025-01-14
        @SerializedName("episode_number")
        val episodeNumber: Int?, // 8
        @SerializedName("episode_type")
        val episodeType: String?, // finale
        @SerializedName("id")
        val id: Int?, // 5716271
        @SerializedName("name")
        val name: String?, // The Real Good Guys
        @SerializedName("overview")
        val overview: String?, // A journey filled with adventure comes to a surprising end.
        @SerializedName("production_code")
        val productionCode: String?,
        @SerializedName("runtime")
        val runtime: Int?, // 39
        @SerializedName("season_number")
        val seasonNumber: Int?, // 1
        @SerializedName("show_id")
        val showId: Int?, // 202879
        @SerializedName("still_path")
        val stillPath: String?, // /eouAk5coqRxaW8UZG77q8qaMsUg.jpg
        @SerializedName("vote_average")
        val voteAverage: Int?, // 7
        @SerializedName("vote_count")
        val voteCount: Int? // 5
    )

    data class Network(
        @SerializedName("id")
        val id: Int?, // 2739
        @SerializedName("logo_path")
        val logoPath: String?, // /gJ8VX6JSu3ciXHuC2dDGAo2lvwM.png
        @SerializedName("name")
        val name: String?, // Disney+
        @SerializedName("origin_country")
        val originCountry: String?
    )

    data class ProductionCompany(
        @SerializedName("id")
        val id: Int?, // 1
        @SerializedName("logo_path")
        val logoPath: String?, // /tlVSws0RvvtPBwViUyOFAO0vcQS.png
        @SerializedName("name")
        val name: String?, // Lucasfilm Ltd.
        @SerializedName("origin_country")
        val originCountry: String? // US
    )

    data class ProductionCountry(
        @SerializedName("iso_3166_1")
        val iso31661: String?, // US
        @SerializedName("name")
        val name: String? // United States of America
    )

    data class Season(
        @SerializedName("air_date")
        val airDate: String?, // 2024-12-02
        @SerializedName("episode_count")
        val episodeCount: Int?, // 8
        @SerializedName("id")
        val id: Int?, // 295165
        @SerializedName("name")
        val name: String?, // Season 1
        @SerializedName("overview")
        val overview: String?, // When four kids make a mysterious discovery on their seemingly safe home planet, they get lost in a strange and dangerous galaxy. Finding their way home, meeting unlikely allies and enemies will be a greater adventure than they ever imagined.
        @SerializedName("poster_path")
        val posterPath: String?, // /f05sgCPYJvYcy28xwyGX15OMyLR.jpg
        @SerializedName("season_number")
        val seasonNumber: Int?, // 1
        @SerializedName("vote_average")
        val voteAverage: Double? // 6.8
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