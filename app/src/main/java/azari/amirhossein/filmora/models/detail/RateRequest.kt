package azari.amirhossein.filmora.models.detail


import com.google.gson.annotations.SerializedName

data class RateRequest(
    @SerializedName("value")
    val value: Double? // 8.5
)