package azari.amirhossein.filmora.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import azari.amirhossein.filmora.data.models.preferences.ContentPreferences
import azari.amirhossein.filmora.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore instance for saving user preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DataStore.NAME)

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        // Preference keys for session, movies, and TV data
        val SESSION_ID = stringPreferencesKey(Constants.DataStore.SESSION_ID)
        val GUEST_SESSION_ID = stringPreferencesKey(Constants.DataStore.GUEST_SESSION_ID)
        val ARE_PREFERENCES_SET = booleanPreferencesKey(Constants.DataStore.ARE_PREFERENCES_SET)

        val SELECTED_MOVIE_IDS = stringPreferencesKey(Constants.DataStore.SELECTED_MOVIE_IDS)
        val FAVORITE_MOVIE_GENRES = stringPreferencesKey(Constants.DataStore.FAVORITE_MOVIE_GENRES)
        val DISLIKED_MOVIE_GENRES = stringPreferencesKey(Constants.DataStore.DISLIKED_MOVIE_GENRES)
        val SELECTED_MOVIE_KEYWORDS = stringPreferencesKey(Constants.DataStore.SELECTED_MOVIE_KEYWORDS)
        val SELECTED_MOVIE_GENRES = stringPreferencesKey(Constants.DataStore.SELECTED_MOVIE_GENRES)

        val SELECTED_TV_IDS = stringPreferencesKey(Constants.DataStore.SELECTED_TV_IDS)
        val FAVORITE_TV_GENRES = stringPreferencesKey(Constants.DataStore.FAVORITE_TV_GENRES)
        val DISLIKED_TV_GENRES = stringPreferencesKey(Constants.DataStore.DISLIKED_TV_GENRES)
        val SELECTED_TV_KEYWORDS = stringPreferencesKey(Constants.DataStore.SELECTED_TV_KEYWORDS)
        val SELECTED_TV_GENRES = stringPreferencesKey(Constants.DataStore.SELECTED_TV_GENRES)
    }

    // --- Session management ---
    // Save session ID and remove guest session if exists
    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_ID] = sessionId
            preferences.remove(GUEST_SESSION_ID)
        }
    }

    // Save guest session ID and remove normal session if exists
    suspend fun saveGuestSessionId(guestSessionId: String) {
        context.dataStore.edit { preferences ->
            preferences[GUEST_SESSION_ID] = guestSessionId
            preferences.remove(SESSION_ID)
        }
    }

    // Get active session ID (normal or guest)
    val activeSessionId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SESSION_ID] ?: preferences[GUEST_SESSION_ID]
        }

    // Mark whether preferences setup is completed
    suspend fun setPreferencesCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ARE_PREFERENCES_SET] = completed
        }
    }

    // Observe if preferences are set
    val arePreferencesSet: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ARE_PREFERENCES_SET] ?: false
        }

    // --- Movie Preferences ---
    // Get stored movie preferences as a Flow
    fun getMoviePreferences(): Flow<ContentPreferences> = context.dataStore.data.map { prefs ->
        ContentPreferences(
            selectedIds = prefs[SELECTED_MOVIE_IDS]?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?: emptyList(),
            favoriteGenres = prefs[FAVORITE_MOVIE_GENRES]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
            dislikedGenres = prefs[DISLIKED_MOVIE_GENRES]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
            selectedKeywords = prefs[SELECTED_MOVIE_KEYWORDS]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
            selectedGenres = prefs[SELECTED_MOVIE_GENRES]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        )
    }

    // Save movie preferences
    suspend fun saveMoviePreferences(preferences: ContentPreferences) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_MOVIE_IDS] = preferences.selectedIds.joinToString(",")
            prefs[FAVORITE_MOVIE_GENRES] = preferences.favoriteGenres.joinToString(",")
            prefs[DISLIKED_MOVIE_GENRES] = preferences.dislikedGenres.joinToString(",")
            prefs[SELECTED_MOVIE_KEYWORDS] = preferences.selectedKeywords.joinToString(",")
            prefs[SELECTED_MOVIE_GENRES] = preferences.selectedGenres.joinToString(",")
        }
    }

    // --- TV Preferences ---
    // Get stored TV preferences as a Flow
    fun getTvPreferences(): Flow<ContentPreferences> = context.dataStore.data.map { prefs ->
        ContentPreferences(
            selectedIds = prefs[SELECTED_TV_IDS]?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?: emptyList(),
            favoriteGenres = prefs[FAVORITE_TV_GENRES]?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet(),
            dislikedGenres = prefs[DISLIKED_TV_GENRES]?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet(),
            selectedKeywords = prefs[SELECTED_TV_KEYWORDS]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
            selectedGenres = prefs[SELECTED_TV_GENRES]?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet()
        )
    }

    // Save TV preferences
    suspend fun saveTvPreferences(preferences: ContentPreferences) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_TV_IDS] = preferences.selectedIds.joinToString(",")
            prefs[FAVORITE_TV_GENRES] = preferences.favoriteGenres.joinToString(",")
            prefs[DISLIKED_TV_GENRES] = preferences.dislikedGenres.joinToString(",")
            prefs[SELECTED_TV_KEYWORDS] = preferences.selectedKeywords.joinToString(",")
            prefs[SELECTED_TV_GENRES] = preferences.selectedGenres.joinToString(",")
        }
    }

    // Clear both session IDs
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_ID)
            preferences.remove(GUEST_SESSION_ID)
        }
    }
}
