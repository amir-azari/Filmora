package azari.amirhossein.filmora.models.authentication


import com.google.gson.annotations.SerializedName

data class ResponseSession(
    @SerializedName("failure")
    val failure: Boolean?,
    @SerializedName("status_code")
    val statusCode: Int?,
    @SerializedName("status_message")
    val statusMessage: String?,

    @SerializedName("success")
    val success: Boolean? ,
    @SerializedName("session_id")
    val sessionId: String?
)