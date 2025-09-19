package azari.amirhossein.filmora.data.models.authentication


import com.google.gson.annotations.SerializedName

data class CreateGuestSession(
    @SerializedName("expires_at")
    val expiresAt: String?, // 2025-09-13 18:07:06 UTC
    @SerializedName("guest_session_id")
    val guestSessionId: String?, // fd41250b8ab48fcf0d3b5876e31c9c72
    @SerializedName("success")
    val success: Boolean? // true
)