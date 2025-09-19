package azari.amirhossein.filmora.data.models.authentication


import com.google.gson.annotations.SerializedName

data class RequestTokenResponse(
    @SerializedName("expires_at")
    val expiresAt: String?, // 2024-12-02 18:31:34 UTC
    @SerializedName("request_token")
    val requestToken: String?,
    @SerializedName("success")
    val success: Boolean?, // true , false
)