package azari.amirhossein.filmora.models.authentication

import com.google.gson.annotations.SerializedName

data class ResponseToken(
    @SerializedName("expires_at")
    val expiresAt: String?, // 2024-12-02 18:31:34 UTC
    @SerializedName("request_token")
    val requestToken: String?,
    @SerializedName("success")
    val success: Boolean?, // true , false

    @SerializedName("status_code")
    val statusCode: Int?, // 30
    @SerializedName("status_message")
    val statusMessage: String?, // Invalid username and/or password: You did not provide a valid login.

)