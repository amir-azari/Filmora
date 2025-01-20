package azari.amirhossein.filmora.models.detail

import com.google.gson.annotations.SerializedName

data class Network(
    @SerializedName("id")
    val id: Int?, // 2739
    @SerializedName("logo_path")
    val logoPath: String?, // /gJ8VX6JSu3ciXHuC2dDGAo2lvwM.png
    @SerializedName("name")
    val name: String?, // Disney+
    @SerializedName("origin_country")
    val originCountry: String?
)