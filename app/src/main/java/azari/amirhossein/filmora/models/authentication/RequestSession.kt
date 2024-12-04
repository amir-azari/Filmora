package azari.amirhossein.filmora.models.authentication


import com.google.gson.annotations.SerializedName

data class RequestSession(
    @SerializedName("request_token")
    var requestToken: String? = null
)