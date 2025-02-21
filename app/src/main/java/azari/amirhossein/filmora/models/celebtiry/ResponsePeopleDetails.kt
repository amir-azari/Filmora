package azari.amirhossein.filmora.models.celebtiry


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponsePeopleDetails(
    @SerializedName("adult")
    val adult: Boolean?, // false
    @SerializedName("also_known_as")
    val alsoKnownAs: List<String?>?,
    @SerializedName("biography")
    val biography: String?, // Thomas Cruise Mapother IV (born July 3, 1962) is an American actor and producer. Regarded as a Hollywood icon, he has received various accolades, including an Honorary Palme d'Or, three Golden Globe Awards, and nominations for four Academy Awards. His films have grossed over $12 billion worldwide, placing him among the highest-grossing actors ever. He is one of Hollywood's most bankable stars and consistently one of the world's highest-paid actors.Cruise began acting in the early 1980s and made his breakthrough with leading roles in Risky Business (1983) and Top Gun (1986). Critical acclaim came with his roles in the dramas The Color of Money (1986), Rain Man (1988), and Born on the Fourth of July (1989). He won a Golden Globe Award for portraying Ron Kovic in the latter. He received a nomination for the Academy Award for Best Actor. As a leading Hollywood star in the 1990s, he starred in commercially successful films, including the drama A Few Good Men (1992), the thriller The Firm (1993), the horror film Interview with the Vampire (1994), and the romance Jerry Maguire (1996). For the latter, he won a Golden Globe Award for Best Actor. Cruise's performance in the drama Magnolia (1999) earned him another Golden Globe Award and a nomination for the Academy Award for Best Supporting Actor.Cruise subsequently became a science fiction and action film star, often performing his risky stunts. He has played fictional agent Ethan Hunt in the Mission: Impossible film series since 1996. His other films in the genre include Vanilla Sky (2001), Minority Report (2002), The Last Samurai (2003), Collateral (2004), War of the Worlds (2005), Knight and Day (2010), Jack Reacher (2012), Oblivion (2013), Edge of Tomorrow (2014), and Top Gun: Maverick (2022).Cruise holds the Guinness World Record for the most consecutive $100-million-grossing movies, a feat achieved from 2012 to 2018. In December 2024, he was awarded the US Navy's highest civilian honour, the Distinguished Public Service Award, for his "outstanding contributions" to the military with his screen roles. Forbes ranked him as the world's most powerful celebrity in 2006. He was named People's Sexiest Man Alive in 1990 and received the top honour of "Most Beautiful People" in 1997. Outside his film career, Cruise has been an outspoken advocate for the Church of Scientology.Description above from the Wikipedia article Tom Cruise, licensed under CC-BY-SA, full list of contributors on Wikipedia.
    @SerializedName("birthday")
    val birthday: String?, // 1962-07-03
    @SerializedName("deathday")
    val deathday: String?, // null
    @SerializedName("gender")
    val gender: Int?, // 2
    @SerializedName("homepage")
    val homepage: String?, // http://www.tomcruise.com
    @SerializedName("id")
    val id: Int?, // 500
    @SerializedName("images")
    val images: Images?,
    @SerializedName("imdb_id")
    val imdbId: String?, // nm0000129
    @SerializedName("known_for_department")
    val knownForDepartment: String?, // Acting
    @SerializedName("movie_credits")
    val movieCredits: MovieCredits?,
    @SerializedName("name")
    val name: String?, // Tom Cruise
    @SerializedName("place_of_birth")
    val placeOfBirth: String?, // Syracuse, New York, USA
    @SerializedName("popularity")
    val popularity: Double?, // 154.479
    @SerializedName("profile_path")
    val profilePath: String?, // /8qBylBsQf4llkGrWR3qAsOtOU8O.jpg
    @SerializedName("tv_credits")
    val tvCredits: TvCredits?
) : Parcelable {
    @Parcelize
    data class Images(
        @SerializedName("profiles")
        val profiles: List<Profile?>?
    ) : Parcelable {
        @Parcelize

        data class Profile(
            @SerializedName("aspect_ratio")
            val aspectRatio: Double?, // 0.667
            @SerializedName("file_path")
            val filePath: String?, // /8qBylBsQf4llkGrWR3qAsOtOU8O.jpg
            @SerializedName("height")
            val height: Int?, // 2100
            @SerializedName("iso_639_1")
            val iso6391: String?, // null
            @SerializedName("vote_average")
            val voteAverage: Double?, // 4.986
            @SerializedName("vote_count")
            val voteCount: Int?, // 72
            @SerializedName("width")
            val width: Int? // 1400
        ) : Parcelable
    }
    @Parcelize
    data class MovieCredits(
        @SerializedName("cast")
        val cast: List<Cast?>?,
        @SerializedName("crew")
        val crew: List<Crew?>?
    ) : Parcelable {
        @Parcelize
        data class Cast(
            @SerializedName("adult")
            val adult: Boolean?, // false
            @SerializedName("backdrop_path")
            val backdropPath: String?, // /nH6hPhJq3EEv9CnBZgXU3IQnpJo.jpg
            @SerializedName("character")
            val character: String?, // Ray Ferrier
            @SerializedName("credit_id")
            val creditId: String?, // 52fe4213c3a36847f800226b
            @SerializedName("genre_ids")
            val genreIds: List<Int>?,
            @SerializedName("id")
            val id: Int, // 74
            @SerializedName("order")
            val order: Int?, // 0
            @SerializedName("original_language")
            val originalLanguage: String?, // en
            @SerializedName("original_title")
            val originalTitle: String?, // War of the Worlds
            @SerializedName("overview")
            val overview: String?, // Ray Ferrier is a divorced dockworker and less-than-perfect father. Soon after his ex-wife and her new husband drop off his teenage son and young daughter for a rare weekend visit, a strange and powerful lightning storm touches down.
            @SerializedName("popularity")
            val popularity: Double?, // 52.59
            @SerializedName("poster_path")
            val posterPath: String?, // /6Biy7R9LfumYshur3YKhpj56MpB.jpg
            @SerializedName("release_date")
            val releaseDate: String?, // 2005-06-13
            @SerializedName("title")
            val title: String?, // War of the Worlds
            @SerializedName("video")
            val video: Boolean?, // false
            @SerializedName("vote_average")
            val voteAverage: Double?, // 6.514
            @SerializedName("vote_count")
            val voteCount: Int? // 8462
        ) : Parcelable

        @Parcelize

        data class Crew(
            @SerializedName("adult")
            val adult: Boolean?, // false
            @SerializedName("backdrop_path")
            val backdropPath: String?, // /z354BaTVzKj7E60WLzDoSmUuO4u.jpg
            @SerializedName("credit_id")
            val creditId: String?, // 52fe425ec3a36847f8018e1f
            @SerializedName("department")
            val department: String?, // Production
            @SerializedName("genre_ids")
            val genreIds: List<Int>?,
            @SerializedName("id")
            val id: Int, // 616
            @SerializedName("job")
            val job: String?, // Producer
            @SerializedName("original_language")
            val originalLanguage: String?, // en
            @SerializedName("original_title")
            val originalTitle: String?, // The Last Samurai
            @SerializedName("overview")
            val overview: String?, // Nathan Algren is an American hired to instruct the Japanese army in the ways of modern warfare, which finds him learning to respect the samurai and the honorable principles that rule them. Pressed to destroy the samurai's way of life in the name of modernization and open trade, Algren decides to become an ultimate warrior himself and to fight for their right to exist.
            @SerializedName("popularity")
            val popularity: Double?, // 41.154
            @SerializedName("poster_path")
            val posterPath: String?, // /lsasOSgYI85EHygtT5SvcxtZVYT.jpg
            @SerializedName("release_date")
            val releaseDate: String?, // 2003-12-05
            @SerializedName("title")
            val title: String?, // The Last Samurai
            @SerializedName("video")
            val video: Boolean?, // false
            @SerializedName("vote_average")
            val voteAverage: Double?, // 7.6
            @SerializedName("vote_count")
            val voteCount: Int? // 6928
        ) : Parcelable
    }
    @Parcelize

    data class TvCredits(
        @SerializedName("cast")
        val cast: List<Cast?>?,
        @SerializedName("crew")
        val crew: List<Crew?>?
    ) : Parcelable {
        @Parcelize

        data class Cast(
            @SerializedName("adult")
            val adult: Boolean?, // false
            @SerializedName("backdrop_path")
            val backdropPath: String?, // /vcVOtGnGA4NkCBFbKEvlBD226c8.jpg
            @SerializedName("character")
            val character: String?,
            @SerializedName("credit_id")
            val creditId: String?, // 525392e319c29579402a7e0c
            @SerializedName("episode_count")
            val episodeCount: Int?, // 1
            @SerializedName("first_air_date")
            val firstAirDate: String?, // 1989-08-03
            @SerializedName("genre_ids")
            val genreIds: List<Int>?,
            @SerializedName("id")
            val id: Int, // 742
            @SerializedName("name")
            val name: String?, // Prime Time LIVE
            @SerializedName("origin_country")
            val originCountry: List<String>?,
            @SerializedName("original_language")
            val originalLanguage: String?, // en
            @SerializedName("original_name")
            val originalName: String?, // Primetime
            @SerializedName("overview")
            val overview: String?, // Primetime is an American news magazine show which debuted on ABC in 1989 with co-hosts Sam Donaldson and Diane Sawyer and originally had the title Primetime Live.
            @SerializedName("popularity")
            val popularity: Double?, // 134.584
            @SerializedName("poster_path")
            val posterPath: String?, // /3ne3uDf9kPpN6DO9lJDBD7lF7eI.jpg
            @SerializedName("vote_average")
            val voteAverage: Double?, // 3.5
            @SerializedName("vote_count")
            val voteCount: Int? // 2
        ) : Parcelable
        @Parcelize

        data class Crew(
            @SerializedName("adult")
            val adult: Boolean?, // false
            @SerializedName("backdrop_path")
            val backdropPath: String?, // /lPTfmwxZtauvTuqKls8EBLJeW2j.jpg
            @SerializedName("credit_id")
            val creditId: String?, // 52577c9b760ee36aaa5969c6
            @SerializedName("department")
            val department: String?, // Directing
            @SerializedName("episode_count")
            val episodeCount: Int?, // 1
            @SerializedName("first_air_date")
            val firstAirDate: String?, // 1993-08-01
            @SerializedName("genre_ids")
            val genreIds: List<Int>?,
            @SerializedName("id")
            val id: Int, // 4981
            @SerializedName("job")
            val job: String?, // Director
            @SerializedName("name")
            val name: String?, // Fallen Angels
            @SerializedName("origin_country")
            val originCountry: List<String>?,
            @SerializedName("original_language")
            val originalLanguage: String?, // en
            @SerializedName("original_name")
            val originalName: String?, // Fallen Angels
            @SerializedName("overview")
            val overview: String?, // A neo-noir anthology television series, set in somber Los Angeles right after World War II and before the election of American President John F. Kennedy. The episodes, although filmed in color, mimicked what had been done by Hollywood filmmakers during the film noir era of the 1940s and 1950s in terms of tone, look, and story content.
            @SerializedName("popularity")
            val popularity: Double?, // 44.133
            @SerializedName("poster_path")
            val posterPath: String?, // /fnbdv4uSFUGJ2jp9Gf99fYtlIu3.jpg
            @SerializedName("vote_average")
            val voteAverage: Double?, // 5.5
            @SerializedName("vote_count")
            val voteCount: Int? // 10
        ) : Parcelable
    }
}