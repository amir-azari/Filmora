package azari.amirhossein.filmora.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import azari.amirhossein.filmora.models.acccount.ResponseAccountDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = "account_details")

    private object Keys {
        val ID = intPreferencesKey("id")
        val USERNAME = stringPreferencesKey("username")
        val NAME = stringPreferencesKey("name")
        val INCLUDE_ADULT = booleanPreferencesKey("include_adult")
        val ISO_3166_1 = stringPreferencesKey("iso_3166_1")
        val ISO_639_1 = stringPreferencesKey("iso_639_1")
    }

    suspend fun saveAccountDetails(account: ResponseAccountDetails) {
        context.accountDataStore.edit { prefs ->
            account.id?.let { prefs[Keys.ID] = it }
            prefs[Keys.USERNAME] = account.username ?: ""
            prefs[Keys.NAME] = account.name ?: ""
            account.includeAdult?.let { prefs[Keys.INCLUDE_ADULT] = it }
            prefs[Keys.ISO_3166_1] = account.iso31661 ?: ""
            prefs[Keys.ISO_639_1] = account.iso6391 ?: ""
        }
    }

    fun getCachedAccountDetails(): Flow<ResponseAccountDetails?> =
        context.accountDataStore.data.map { preferences ->
            val id = preferences[Keys.ID]
            val username = preferences[Keys.USERNAME]
            val name = preferences[Keys.NAME]
            val includeAdult = preferences[Keys.INCLUDE_ADULT]
            val iso31661 = preferences[Keys.ISO_3166_1]
            val iso6391 = preferences[Keys.ISO_639_1]
            if (id != null && !username.isNullOrEmpty()) {
                ResponseAccountDetails(
                    id = id,
                    username = username,
                    name = name,
                    includeAdult = includeAdult,
                    iso31661 = iso31661,
                    iso6391 = iso6391,
                    avatar = null
                )
            } else {
                null
            }
        }
}
