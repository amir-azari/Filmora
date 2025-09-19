package azari.amirhossein.filmora.ui.splash

sealed class SplashNavigation {
    object ToLogin : SplashNavigation()
    object ToPreferences : SplashNavigation()
    object ToHome : SplashNavigation()
}