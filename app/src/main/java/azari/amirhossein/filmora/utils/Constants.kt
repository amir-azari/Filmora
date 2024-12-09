package azari.amirhossein.filmora.utils

object Constants {


    object Network{
        // Base URL for API
        const val BASE_URL = "https://api.themoviedb.org/3/"

        // API Key
        const val API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2YzQ2NDc2YjFlZDlkMDU2OGE0OTBkYjIyMWRhOGRlZiIsIm5iZiI6MTczMjI2NzQ5OS43OTYsInN1YiI6IjY3NDA0ZGViMzJhOWFhZjQzZDk2N2YxOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Xs9eMGU6frRAowl3sz5HDaov_GSAopNND7z4s30nfJc"

        // Timeouts
        const val CONNECT_TIMEOUT = 30L
        const val READ_TIMEOUT = 30L
        const val WRITE_TIMEOUT = 30L

        // Common Headers
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val HEADER_ACCEPT = "accept"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val CONTENT_TYPE_JSON = "application/json"
    }

    object DataStore{
        const val SESSION_ID = "session_id"
        const val IS_GUEST = "is_guest"
        const val PROFILE = "profile"
    }

    object WebView{
        const val RESET_PASSWORD_URL = "https://www.themoviedb.org/reset-password"
        const val SIGNUP_URL = "https://www.themoviedb.org/signup"

        fun getHttpErrorMessages(): Map<Int, String> {
            return mapOf(
                400 to "Bad Request: The server could not understand the request.",
                401 to "Unauthorized: You need to log in.",
                403 to "Forbidden: You do not have permission to access this resource.",
                404 to "Not Found: The requested page was not found on the server.",
                405 to "Method Not Allowed: The HTTP method used is not allowed.",
                408 to "Request Timeout: The server took too long to respond.",
                429 to "Too Many Requests: You've sent too many requests in a given amount of time.",
                500 to "Internal Server Error: There was an issue on the server side.",
                501 to "Not Implemented: The server does not support the functionality required.",
                502 to "Bad Gateway: The server received an invalid response from the upstream server.",
                503 to "Service Unavailable: The server is temporarily unavailable.",
                504 to "Gateway Timeout: The server did not receive a timely response from another server."
            )
        }
    }

    object BundleKey{
        const val URL_BUNDLE_KEY = "url"
    }

}