package azari.amirhossein.filmora.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import azari.amirhossein.filmora.data.repository.LoginRepository
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.viewmodel.LoginViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {
    private object StoredKey {
        val SESSION_ID = stringPreferencesKey(Constants.DataStore.SESSION_ID)
    }

    private val Context.dataStore :DataStore<Preferences> by preferencesDataStore(Constants.DataStore.PROFILE)

    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { prefs ->
            prefs[StoredKey.SESSION_ID] = sessionId
        }
    }

    fun getSessionId(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[StoredKey.SESSION_ID]
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(StoredKey.SESSION_ID)
        }
    }

}
