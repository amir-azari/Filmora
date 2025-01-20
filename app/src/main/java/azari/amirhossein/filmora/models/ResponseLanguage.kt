package azari.amirhossein.filmora.models


import com.google.gson.annotations.SerializedName

class ResponseLanguage : ArrayList<ResponseLanguage.ResponseLanguageItem>(){
    data class ResponseLanguageItem(
        @SerializedName("english_name")
        val englishName: String?, // Cornish
        @SerializedName("iso_639_1")
        val iso6391: String?, // kw
        @SerializedName("name")
        val name: String?
    )
}