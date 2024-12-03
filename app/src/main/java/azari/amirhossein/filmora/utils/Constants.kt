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

}