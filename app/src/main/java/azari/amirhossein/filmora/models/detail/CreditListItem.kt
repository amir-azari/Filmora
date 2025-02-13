package azari.amirhossein.filmora.models.detail

sealed class CreditListItem {
    data class DepartmentHeader(val name: String) : CreditListItem() {
        override fun getItemId(): String = "dept_$name"
    }

    data class YearHeader(val year: String) : CreditListItem() {
        override fun getItemId(): String = "year_$year"
    }

    data class CreditEntry(val credit: CreditItem) : CreditListItem() {
        override fun getItemId(): String = "credit_${credit.id}"
    }

    abstract fun getItemId(): String
}

data class DepartmentSection(
    val name: String,
    val yearSections: List<YearSection>
)

data class YearSection(
    val year: String,
    val items: List<CreditItem>
)