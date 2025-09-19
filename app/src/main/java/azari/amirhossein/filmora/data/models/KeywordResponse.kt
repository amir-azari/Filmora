package azari.amirhossein.filmora.data.models


import com.google.gson.annotations.SerializedName

data class KeywordResponse(
    @SerializedName("id")
    val id: Int = 0, // 232230
    @SerializedName("keywords", alternate = ["results"])
    val keywords: List<Keyword> = emptyList()
) {
    data class Keyword(
        @SerializedName("id")
        val id: Int = 0, // 2035
        @SerializedName("name")
        val name: String = "" // mythology
    )
}