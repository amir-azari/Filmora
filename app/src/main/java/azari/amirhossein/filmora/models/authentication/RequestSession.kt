package azari.amirhossein.filmora.models.authentication


import com.google.gson.annotations.SerializedName

data class RequestSession(
    @SerializedName("request_token")
    val requestToken: String? // 1a07dfec25014dc031560f7facc0683fd552e66b
)