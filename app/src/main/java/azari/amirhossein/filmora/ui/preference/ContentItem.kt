package azari.amirhossein.filmora.ui.preference

data class ContentItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val genreIds: List<Int>,
    val releaseDate: String?,
    val voteAverage: Double
)