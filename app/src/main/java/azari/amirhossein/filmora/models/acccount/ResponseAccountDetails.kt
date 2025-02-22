package azari.amirhossein.filmora.models.acccount


import com.google.gson.annotations.SerializedName

data class ResponseAccountDetails(
    @SerializedName("avatar")
    val avatar: Avatar?,
    @SerializedName("id")
    val id: Int?, // 21645442
    @SerializedName("include_adult")
    val includeAdult: Boolean?, // false
    @SerializedName("iso_3166_1")
    val iso31661: String?, // IR
    @SerializedName("iso_639_1")
    val iso6391: String?, // en
    @SerializedName("name")
    val name: String?, // Amir
    @SerializedName("username")
    val username: String? // amirAzari
) {
    data class Avatar(
        @SerializedName("gravatar")
        val gravatar: Gravatar?,
        @SerializedName("tmdb")
        val tmdb: Tmdb?
    ) {
        data class Gravatar(
            @SerializedName("hash")
            val hash: String? // 66a6a634cc919c33ea21d6efece68383
        )

        data class Tmdb(
            @SerializedName("avatar_path")
            val avatarPath: Any? // null
        )
    }
}