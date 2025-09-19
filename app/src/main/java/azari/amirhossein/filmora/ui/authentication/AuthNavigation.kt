package azari.amirhossein.filmora.ui.authentication

sealed class AuthNavigation {
    object ToPreferences : AuthNavigation()
    object ToHome : AuthNavigation()
}