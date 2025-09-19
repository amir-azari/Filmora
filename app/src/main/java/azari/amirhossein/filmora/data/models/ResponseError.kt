package azari.amirhossein.filmora.data.models


import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("status_code")
    val statusCode: Int?, // 34
    @SerializedName("status_message")
    val statusMessage: String?, // The resource you requested could not be found.
    @SerializedName("success")
    val success: Boolean? // false
)