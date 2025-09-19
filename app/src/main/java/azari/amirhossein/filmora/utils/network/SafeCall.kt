package azari.amirhossein.filmora.utils.network

import retrofit2.HttpException
import java.io.IOException

inline fun <T> safeCall(action: () -> T): Resource<T> {
    return try {
        Resource.Success(action())
    } catch (e: IOException) {
        Resource.Error(Failure.NetworkConnection)
    } catch (e: HttpException) {
        Resource.Error(Failure.ServerError)
    } catch (e: Exception) {
        Resource.Error(Failure.UnknownError)
    }
}