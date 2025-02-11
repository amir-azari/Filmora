package azari.amirhossein.filmora.models.celebtiry


import com.google.gson.annotations.SerializedName

data class ResponseTrendingCelebrity(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int?, // 225
    @SerializedName("total_results")
    val totalResults: Int? // 4485
) {
    data class Result(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("gender")
        val gender: Int?, // 2
        @SerializedName("id")
        val id: Int, // 57353
        @SerializedName("known_for_department")
        val knownForDepartment: String?, // Acting
        @SerializedName("media_type")
        val mediaType: String?, // person
        @SerializedName("name")
        val name: String?, // David Graf
        @SerializedName("original_name")
        val originalName: String?, // David Graf
        @SerializedName("popularity")
        val popularity: Double?, // 16.423
        @SerializedName("profile_path")
        val profilePath: String? // /oAVx2Ea0qrND2wn78m2wQtGGlz1.jpg
    )
}