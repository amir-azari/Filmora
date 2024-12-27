package azari.amirhossein.filmora.models.prefences.movie

data class MoviePreferences(
    val selectedMovieIds: List<Int>,
    val favoriteGenres: Set<Int>,
    val dislikedGenres: Set<Int>,
    val selectedMovieKeywords: Set<Int>,
    val selectedMovieGenres: Set<Int>
)