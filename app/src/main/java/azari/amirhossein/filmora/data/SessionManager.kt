package azari.amirhossein.filmora.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {
    private object StoredKey {
        val SESSION_ID = stringPreferencesKey(Constants.DataStore.SESSION_ID)
        val IS_GUEST = booleanPreferencesKey(Constants.DataStore.IS_GUEST)

        val SELECTED_MOVIE_IDS = stringPreferencesKey(Constants.DataStore.SELECTED_MOVIE_IDS)
        val FAVORITE_GENRES = stringPreferencesKey(Constants.DataStore.FAVORITE_GENRES)
        val DISLIKED_GENRES = stringPreferencesKey(Constants.DataStore.DISLIKED_GENRES)
        val SELECTED_MOVIE_KEYWORDS = stringPreferencesKey(Constants.DataStore.SELECTED_MOVIE_KEYWORDS)
        val SELECTED_MOVIE_GENRES = stringPreferencesKey(Constants.DataStore.SELECTED_MOVIE_GENRES)

    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.DataStore.PROFILE)

    suspend fun saveSessionId(sessionId: String, isGuest: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[StoredKey.SESSION_ID] = sessionId
            prefs[StoredKey.IS_GUEST] = isGuest
        }
    }

    fun getSessionId(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[StoredKey.SESSION_ID]
    }

    fun isGuest(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[StoredKey.IS_GUEST] ?: false
    }

    suspend fun saveMoviePreferences(preferences: TvAndMoviePreferences) {
        context.dataStore.edit { prefs ->
            prefs[StoredKey.SELECTED_MOVIE_IDS] = preferences.selectedIds.joinToString(",")
            prefs[StoredKey.FAVORITE_GENRES] = preferences.favoriteGenres.joinToString(",")
            prefs[StoredKey.DISLIKED_GENRES] = preferences.dislikedGenres.joinToString(",")
            prefs[StoredKey.SELECTED_MOVIE_KEYWORDS] = preferences.selectedKeywords.joinToString(",")
            prefs[StoredKey.SELECTED_MOVIE_GENRES] = preferences.selectedGenres.joinToString(",")
        }
    }
    fun getMoviePreferences(): Flow<TvAndMoviePreferences> = context.dataStore.data.map { preferences ->
        TvAndMoviePreferences(
            selectedIds = preferences[StoredKey.SELECTED_MOVIE_IDS]?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
            favoriteGenres = preferences[StoredKey.FAVORITE_GENRES]?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
            dislikedGenres = preferences[StoredKey.DISLIKED_GENRES]?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
            selectedKeywords = preferences[StoredKey.SELECTED_MOVIE_KEYWORDS]?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
            selectedGenres = preferences[StoredKey.SELECTED_MOVIE_GENRES]?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        )
    }
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(StoredKey.SESSION_ID)
            preferences.remove(StoredKey.IS_GUEST)
        }
    }
}
