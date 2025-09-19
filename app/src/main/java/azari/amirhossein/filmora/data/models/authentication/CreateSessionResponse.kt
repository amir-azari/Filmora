package azari.amirhossein.filmora.data.models.authentication


import com.google.gson.annotations.SerializedName

data class CreateSessionResponse(
    @SerializedName("session_id")
    val sessionId: String?, // cd41bef1f01d424d164bfe778a2f9d5e67bacc49
    @SerializedName("success")
    val success: Boolean? // true
)