package azari.amirhossein.filmora.utils

object Constants {

    object Network {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

        const val CONNECT_TIMEOUT = 60L
        const val HEADER_AUTHORIZATION = "authorization"
        const val HEADER_BEARER = "Bearer"
    }

    object Database{
        const val DATABASE_NAME = "filmora_database"
    }

    object ImageSize {
        const val W500 = "w500"
        const val ORIGINAL = "original"
    }

    object Web {
        const val RESET_PASSWORD_URL = "https://www.themoviedb.org/reset-password"
        const val SIGNUP_URL = "https://www.themoviedb.org/signup"
    }
    object DataStore {
        const val NAME = "user_preferences"

        const val SESSION_ID = "session_id"
        const val GUEST_SESSION_ID = "guest_session_id"
        const val ARE_PREFERENCES_SET = "are_preferences_set"

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
    object Args {
        const val SELECTION_TYPE = "selectionType"
    }
    object UI {
        const val TOOLBAR_TITLE_MOVIES = "Movies"
        const val TOOLBAR_TITLE_TV_SHOWS = "TV Shows"
        const val TOOLBAR_TITLE_CELEBRITIES = "Celebrities"
    }

}