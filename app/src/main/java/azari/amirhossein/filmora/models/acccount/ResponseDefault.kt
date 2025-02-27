package azari.amirhossein.filmora.models.acccount

import com.google.gson.annotations.SerializedName

data class ResponseDefault(
    @SerializedName("status_code")
    val statusCode: Int?, // 1
    @SerializedName("status_message")
    val statusMessage: String?, // Success.
    @SerializedName("success")
    val success: Boolean? // true
)