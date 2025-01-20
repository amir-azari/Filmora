package azari.amirhossein.filmora.models.detail

import com.google.gson.annotations.SerializedName

data class SpokenLanguage(
    @SerializedName("english_name")
    val englishName: String?, // English
    @SerializedName("iso_639_1")
    val iso6391: String?, // en
    @SerializedName("name")
    val name: String? // English
)