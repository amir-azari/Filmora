package azari.amirhossein.filmora.ui.preference

import azari.amirhossein.filmora.data.models.GenreResponse.Genre
import azari.amirhossein.filmora.utils.Event
import azari.amirhossein.filmora.R
import androidx.annotation.StringRes
import azari.amirhossein.filmora.utils.view.UiText

data class PreferencesUiState(
    val selectionType: SelectionType = SelectionType.MOVIE,

    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val isGenresLoading: Boolean = false,

    val genres: List<Genre> = emptyList(),
    val searchResults: List<ContentItem> = emptyList(),
    val selectedItems: List<ContentItem> = emptyList(),

    val selectedFavoriteGenreIds: Set<Int> = emptySet(),
    val selectedDislikedGenreIds: Set<Int> = emptySet(),

    @StringRes val headerTitleResId: Int = R.string.your_movie_taste,
    @StringRes val selectionPromptResId: Int = R.string.choose_your_favorite_movies,
    @StringRes val searchHintResId: Int = R.string.search_movies,
    @StringRes val selectedItemsTitleResId: Int = R.string.selected_movies,
    @StringRes val buttonTextResId: Int = R.string.next,

    val userMessage : Event<UiText>? = null,
    val navigationEvent: Event<PreferencesNavigation >? = null

)
