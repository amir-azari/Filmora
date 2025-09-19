package azari.amirhossein.filmora.data.models.preferences

data class ContentPreferences(
    val selectedIds: List<Int>,
    val favoriteGenres: Set<Int>,
    val dislikedGenres: Set<Int>,
    val selectedKeywords: Set<Int>,
    val selectedGenres: Set<Int>
)