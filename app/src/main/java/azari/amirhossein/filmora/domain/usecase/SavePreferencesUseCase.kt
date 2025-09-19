package azari.amirhossein.filmora.domain.usecase

import azari.amirhossein.filmora.data.models.preferences.ContentPreferences
import azari.amirhossein.filmora.data.repository.UserPreferencesRepository
import azari.amirhossein.filmora.domain.repository.ContentRepository
import azari.amirhossein.filmora.ui.preference.ContentItem
import azari.amirhossein.filmora.ui.preference.SelectionType
import azari.amirhossein.filmora.utils.network.Resource
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class SavePreferencesUseCase @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(
        type: SelectionType,
        selectedItems: List<ContentItem>,
        favoriteGenreIds: Set<Int>,
        dislikedGenreIds: Set<Int>
    ) {
        val allKeywords = mutableSetOf<Int>()

        selectedItems.forEach { item ->
            val keywordsFlow = when (type) {
                SelectionType.MOVIE -> contentRepository.getMovieKeywords(item.id)
                SelectionType.TV -> contentRepository.getTvKeywords(item.id)
            }

            when (val result = keywordsFlow.dropWhile { it is Resource.Loading }.first()) {
                is Resource.Success -> {
                    result.data.keywords.forEach { keyword ->
                        allKeywords.add(keyword.id)
                    }
                }
                else -> { /* Optional: Log error */ }
            }
        }


        val preferences = ContentPreferences(
            selectedIds = selectedItems.map { it.id },
            favoriteGenres = favoriteGenreIds,
            dislikedGenres = dislikedGenreIds,
            selectedKeywords = allKeywords,
            selectedGenres = selectedItems.flatMap { it.genreIds }.toSet()
        )

        when (type) {
            SelectionType.MOVIE -> userPreferencesRepository.saveMoviePreferences(preferences)
            SelectionType.TV -> {
                userPreferencesRepository.saveTvPreferences(preferences)
                userPreferencesRepository.setPreferencesCompleted(true)
            }

        }
    }
}