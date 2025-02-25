package azari.amirhossein.filmora.models.detail.people

data class CreditItem(
    val id : Int,
    val department: String,
    val showDepartment: Boolean,
    val year: String,
    val title: String,
    val role: String
)