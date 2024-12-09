package azari.amirhossein.filmora.models.authentication


import com.google.gson.annotations.SerializedName

data class ResponseGuestSession(
    @SerializedName("expires_at")
    val expiresAt: String?, // 2024-12-10 19:43:50 UTC
    @SerializedName("guest_session_id")
    val guestSessionId: String?, // a9898a92d24aaf6ab23ed6d85d24c315
    @SerializedName("success")
    val success: Boolean? // true
)