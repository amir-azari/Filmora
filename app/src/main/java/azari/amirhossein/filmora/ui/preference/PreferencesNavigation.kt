package azari.amirhossein.filmora.ui.preference

sealed class PreferencesNavigation  {
    object ToTvPreferences : PreferencesNavigation ()
    object ToMainApp : PreferencesNavigation ()
}