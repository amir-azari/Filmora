package azari.amirhossein.filmora.models.authentication


import com.google.gson.annotations.SerializedName

data class RequestLogin(
    @SerializedName("password")
    var password: String?= null, //
    @SerializedName("request_token")
    var requestToken: String?= null, // d2ac0a7fb849371d39f3ee84e42786f5db1af8f6
    @SerializedName("username")
    var username: String?= null //
)