package azari.amirhossein.filmora.models.detail


import com.google.gson.annotations.SerializedName

data class ResponseImage(
    @SerializedName("backdrops")
    val backdrops: List<Backdrop>?,
    @SerializedName("id")
    val id: Int?, // 839033
    @SerializedName("logos")
    val logos: List<Logo?>?,
    @SerializedName("posters")
    val posters: List<Poster>?
) {
    data class Backdrop(
        @SerializedName("aspect_ratio")
        val aspectRatio: Double?, // 1.778
        @SerializedName("file_path")
        val filePath: String?, // /ie8OSgIHEl6yQiGJ90dsyBWOpQA.jpg
        @SerializedName("height")
        val height: Int?, // 2160
        @SerializedName("iso_639_1")
        val iso6391: String?, // he
        @SerializedName("vote_average")
        val voteAverage: Double?, // 8.034
        @SerializedName("vote_count")
        val voteCount: Int?, // 4
        @SerializedName("width")
        val width: Int? // 3840
    )

    data class Logo(
        @SerializedName("aspect_ratio")
        val aspectRatio: Double?, // 2.122
        @SerializedName("file_path")
        val filePath: String?, // /mxXEi2sI4xhH8QkXo9NiIAAD1JN.png
        @SerializedName("height")
        val height: Int?, // 1298
        @SerializedName("iso_639_1")
        val iso6391: String?, // en
        @SerializedName("vote_average")
        val voteAverage: Double?, // 3.334
        @SerializedName("vote_count")
        val voteCount: Int?, // 2
        @SerializedName("width")
        val width: Int? // 2754
    )

    data class Poster(
        @SerializedName("aspect_ratio")
        val aspectRatio: Double?, // 0.667
        @SerializedName("file_path")
        val filePath: String?, // /ojX9By9jnx4W31sGbArn88HV41q.jpg
        @SerializedName("height")
        val height: Int?, // 3000
        @SerializedName("iso_639_1")
        val iso6391: String?, // de
        @SerializedName("vote_average")
        val voteAverage: Double?, // 8.596
        @SerializedName("vote_count")
        val voteCount: Int?, // 4
        @SerializedName("width")
        val width: Int? // 2000
    )
}