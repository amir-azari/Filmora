package azari.amirhossein.filmora.models.detail

import com.google.gson.annotations.SerializedName

data class ProductionCompany(
    @SerializedName("id")
    val id: Int?, // 4
    @SerializedName("logo_path")
    val logoPath: String?, // /gz66EfNoYPqHTYI4q9UEN4CbHRc.png
    @SerializedName("name")
    val name: String?, // Paramount Pictures
    @SerializedName("origin_country")
    val originCountry: String? // US
)
