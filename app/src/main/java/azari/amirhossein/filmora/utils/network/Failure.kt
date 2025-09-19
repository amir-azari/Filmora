package azari.amirhossein.filmora.utils.network

sealed class Failure {
    object NetworkConnection : Failure()
    object ServerError : Failure()
    data class DatabaseError(val message: String) : Failure()
    data class CustomError(val message: String) : Failure()
    object EmptyResponse : Failure()
    object UnknownError : Failure()
}
