package azari.amirhossein.filmora.models.detail


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseCredit(
    @SerializedName("cast")
    val cast: List<Cast?>?,
    @SerializedName("crew")
    val crew: List<Crew>?,
    @SerializedName("id")
    val id: Int? // 939243
) : Parcelable {
    @Parcelize
    data class Cast(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("cast_id")
        val castId: Int?, // 15
        @SerializedName("character")
        val character: String?, // Ivo Robotnik / Gerald Robotnik
        @SerializedName("credit_id")
        val creditId: String?, // 65982190ea37e007534d578e
        @SerializedName("gender")
        val gender: Int?, // 2
        @SerializedName("id")
        val id: Int, // 206
        @SerializedName("known_for_department")
        val knownForDepartment: String?, // Acting
        @SerializedName("name")
        val name: String?, // Jim Carrey
        @SerializedName("order")
        val order: Int?, // 0
        @SerializedName("original_name")
        val originalName: String?, // Jim Carrey
        @SerializedName("popularity")
        val popularity: Double?, // 62.685
        @SerializedName("profile_path")
        val profilePath: String? // /u0AqTz6Y7GHPCHINS01P7gPvDSb.jpg
    ) : Parcelable

    @Parcelize
    data class Crew(
        @SerializedName("adult")
        val adult: Boolean?, // false
        @SerializedName("credit_id")
        val creditId: String?, // 620c40b6142ef1006db66e7d
        @SerializedName("department")
        val department: String?, // Production
        @SerializedName("gender")
        val gender: Int?, // 2
        @SerializedName("id")
        val id: Int, // 11874
        @SerializedName("job")
        val job: String?, // Producer
        @SerializedName("known_for_department")
        val knownForDepartment: String?, // Production
        @SerializedName("name")
        val name: String?, // Neal H. Moritz
        @SerializedName("original_name")
        val originalName: String?, // Neal H. Moritz
        @SerializedName("popularity")
        val popularity: Double?, // 3.913
        @SerializedName("profile_path")
        val profilePath: String? // /xgCTFHhbQh3QyIT7ZvJGbEohzG8.jpg
    ) : Parcelable
}