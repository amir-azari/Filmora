package azari.amirhossein.filmora.ui.authentication

import azari.amirhossein.filmora.utils.Event
import azari.amirhossein.filmora.utils.view.UiText

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val sessionId: String? = null,
    val currentAction: AuthAction = AuthAction.NONE,
    val userMessage : Event<UiText>? = null,
    val navigationEvent: Event<AuthNavigation>? = null
)
