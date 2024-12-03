package azari.amirhossein.filmora.utils


import retrofit2.Response

open class NetworkResponse<T>(private val response: Response<T>) {

    fun handleNetworkResponse(): NetworkRequest<T> {
        return when {
            response.isSuccessful -> {
                response.body()?.let {
                    NetworkRequest.Success(it)
                } ?: NetworkRequest.Error("Response body is null")
            }
            else -> {
                val errorMessage = response.message() ?: "Unknown error occurred."
                NetworkRequest.Error("Error ${response.code()}: $errorMessage")
            }
        }

    }

}
