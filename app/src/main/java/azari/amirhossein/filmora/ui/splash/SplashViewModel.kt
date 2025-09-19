package azari.amirhossein.filmora.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    userPreferences: UserPreferencesRepository
) : ViewModel() {

    val destination: StateFlow<SplashNavigation?> =
        combine(
            userPreferences.activeSessionId,
            userPreferences.arePreferencesSet
        ) { sessionId, arePreferencesSet ->
            // Decide where to navigate from splash
            when {
                sessionId.isNullOrBlank() -> SplashNavigation.ToLogin
                !arePreferencesSet -> SplashNavigation.ToPreferences
                else -> SplashNavigation.ToHome
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // no destination at the start
        )
}
