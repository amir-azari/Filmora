package azari.amirhossein.filmora.models.detail


import com.google.gson.annotations.SerializedName

data class ResponseCollectionDetails(
    @SerializedName("backdrop_path")
    val backdropPath: String?, // /gn1rdDig7jFPpR8tu3vIXm82E9z.jpg
    @SerializedName("id")
    val id: Int?, // 720879
    @SerializedName("name")
    val name: String?, // Sonic the Hedgehog Collection
    @SerializedName("overview")
    val overview: String?, // Follows the adventures of Sonic the Hedgehog an anthropomorphic hedgehog born with the ability to run faster than the speed of sound, hence his name, and possesses lightning-fast reflexes to match his speed.
    @SerializedName("parts")
    val parts: List<Part>,
    @SerializedName("poster_path")
    val posterPath: String? // /fwFWhYXj8wY6gFACtecJbg229FI.jpg
) {
    data class Part(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("backdrop_path")
        val backdropPath: String?, // /xuLA0pii2IMJW2puT7EvJtgpg0H.jpg
        @SerializedName("genre_ids")
        val genreIds: List<Int?>?,
        @SerializedName("id")
        val id: Int, // 675353
        @SerializedName("media_type")
        val mediaType: String?, // movie
        @SerializedName("original_language")
        val originalLanguage: String?, // en
        @SerializedName("original_title")
        val originalTitle: String?, // Sonic the Hedgehog 2
        @SerializedName("overview")
        val overview: String?, // After settling in Green Hills, Sonic is eager to prove he has what it takes to be a true hero. His test comes when Dr. Robotnik returns, this time with a new partner, Knuckles, in search for an emerald that has the power to destroy civilizations. Sonic teams up with his own sidekick, Tails, and together they embark on a globe-trotting journey to find the emerald before it falls into the wrong hands.
        @SerializedName("popularity")
        val popularity: Double?, // 170.969
        @SerializedName("poster_path")
        val posterPath: String?, // /6DrHO1jr3qVrViUO6s6kFiAGM7.jpg
        @SerializedName("release_date")
        val releaseDate: String?, // 2022-03-30
        @SerializedName("title")
        val title: String?, // Sonic the Hedgehog 2
        @SerializedName("video")
        val video: Boolean?, // false
        @SerializedName("vote_average")
        val voteAverage: Double?, // 7.5
        @SerializedName("vote_count")
        val voteCount: Int? // 5340
    )
}