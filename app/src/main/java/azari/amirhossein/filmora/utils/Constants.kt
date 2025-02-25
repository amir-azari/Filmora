package azari.amirhossein.filmora.utils

object Constants {


    object Network{
        // Base URL for API
        const val BASE_URL = "https://workers-playground-empty-band-cd3f.ah-azari-wr.workers.dev/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

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

        // Movie preferences
        const val SELECTED_MOVIE_IDS = "selected_movie_ids"
        const val FAVORITE_MOVIE_GENRES = "favorite_movie_genres"
        const val DISLIKED_MOVIE_GENRES = "disliked_movie_genres"
        const val SELECTED_MOVIE_KEYWORDS = "selected_movie_keywords"
        const val SELECTED_MOVIE_GENRES = "selected_movie_genres"

        const val SELECTED_TV_IDS = "selected_tv_ids"
        const val FAVORITE_TV_GENRES = "favorite_tv_genres"
        const val DISLIKED_TV_GENRES = "disliked_tv_genres"
        const val SELECTED_TV_KEYWORDS = "selected_tv_keywords"
        const val SELECTED_TV_GENRES = "selected_tv_genres"
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
    object Message{
        const val NO_INTERNET_CONNECTION = "No internet connection"
        const val UNKNOWN_ERROR = "An unknown error occurred"
        const val SESSION_EMPTY = "Session ID is empty"
        const val SESSION_NULL = "Session ID is null"
        const val TOKEN_NULL = "Token is null"
        const val INVALID_MEDIA_TYPE = "Invalid media type."
        const val NO_CACHED_DATA = "No cached data available."
        const val INVALID_DATE = "Invalid date."
    }
    object BundleKey{
        const val URL_BUNDLE_KEY = "url"
    }
    object Discover{
        const val WITH_GENRES = "with_genres"
        const val WITHOUT_GENRES = "without_genres"
        const val WITH_KEYWORDS = "with_keywords"
    }
    object ImageSize {
        const val W500 = "w500"
        const val ORIGINAL = "original"
    }
    object Database{
        const val DATABASE_NAME = "filmora_database"
        const val DETAIL_EXPIRATION_TIME = 60 * 60 * 1000L // 1 hour in milliseconds
        const val HOME_TABLE = "home_table"
        const val MOVIES_TABLE = "movies_table"
        const val MEDIA_DETAIL_TABLE = "media_detail_table"
        const val MOVIE_DETAIL_TABLE = "movie_detail_table"
        const val TV_DETAIL_TABLE = "tv_detail_table"
        const val PEOPLE_DETAIL_TABLE = "People_detail_table"
        const val TV_SHOW_TABLE = "tv_show_table"
        const val CELEBRITIES_TABLE = "celebrities_table"
    }

    object Defaults {
        const val NOT_APPLICABLE = "N/A"
        const val OVERVIEW = "No overview available"
        const val VOTE_COUNT = "0"
        const val VOTE_AVERAGE = 0.0
        const val OVERVIEW_MAX_LINES = 5

        // PeopleDetailFragment
        const val BIOGRAPHY_MAX_LINES = 8
        const val NOT_AVAILABLE = "Not available"
        const val NO_BIOGRAPHY = "No biography available !!"
        const val AGE_UNKNOWN = "Age unknown"
        const val NO_BIRTHDAY_AVAILABLE = "No birthday available"
        const val PLACE_OF_BIRTH_NOT_AVAILABLE = "Place of birth not available"
    }
    object MediaType {
        const val TV = "tv"
        const val MOVIE = "movie"
        const val PEOPLE = "people"
    }
    object SectionType {
        const val POPULAR_MOVIE = "Popular Movie"
        const val POPULAR_TV = "Popular TV"
        const val POPULAR_PEOPLE = "Popular People"
        const val NOW_PLAYING = "Now Playing"
        const val TOP_RATED_MOVIE = "Top Rated Movie"
        const val TOP_RATED_TV = "Top Rated TV"
        const val UPCOMING = "Upcoming"
        const val TRENDING_MOVIE = "Trending Movie"
        const val TRENDING_TV = "Trending TV"
        const val TRENDING_PEOPLE = "Trending People"
        const val AIRING_TODAY = "Airing Today"
        const val ON_THE_AIR = "On The Air"
        const val SECTION_TYPE = "sectionType"
    }
    object Gender {
        const val FEMALE = "Female"
        const val MALE = "Male"
        const val NON_BINARY = "Non-binary"
        const val NOT_SPECIFIED = "Not specified"
    }

    object Formats {
        const val DATE_YYYY_MM_DD = "yyyy-MM-dd"
    }

    object Departments {
        const val ACTING = "Acting"
    }
    object MediaGalleryType {
        const val POSTER = "poster"
        const val BACKDROP = "backdrop"
        const val VIDEO = "video"
    }

    object LoginType {
        const val PROFILE = "profile"
    }
}