package azari.amirhossein.filmora.utils


import org.json.JSONObject
import retrofit2.Response
class NetworkResponse<T>(private val response: Response<T>?) {

    fun handleNetworkResponse(): NetworkRequest<T> {
        return when {
            response == null -> {
                NetworkRequest.Error("No response from server. Please check your internet connection.")
            }
            response.isSuccessful -> {
                response.body()?.let {
                    NetworkRequest.Success(it)
                } ?: NetworkRequest.Error("Response body is null.")
            }
            else -> {
                val errorMessage = extractErrorMessage(response)
                NetworkRequest.Error(errorMessage)
            }
        }
    }

    private fun extractErrorMessage(response: Response<T>?): String {
        return try {
            response?.errorBody()?.string()?.let {
                JSONObject(it).optString("status_message", "Unknown error occurred.")
            } ?: "Unknown error occurred."
        } catch (e: Exception) {
            "Error parsing server response."
        }
    }
}