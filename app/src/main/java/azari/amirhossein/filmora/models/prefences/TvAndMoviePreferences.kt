package azari.amirhossein.filmora.models.prefences

data class TvAndMoviePreferences(
    val selectedIds: List<Int>,
    val favoriteGenres: Set<Int>,
    val dislikedGenres: Set<Int>,
    val selectedKeywords: Set<Int>,
    val selectedGenres: Set<Int>
)