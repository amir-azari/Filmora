package azari.amirhossein.filmora.data.models.authentication

import com.google.gson.annotations.SerializedName

data class CreateSessionRequest(
    @SerializedName("request_token")
    var requestToken: String? = null
)
