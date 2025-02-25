package azari.amirhossein.filmora.models.detail.people

import azari.amirhossein.filmora.models.detail.ResponseCredit

sealed class CastAndCrewListItem {
    data class Header(val title: String) : CastAndCrewListItem() {
        override fun getItemId(): String = "header_$title"
    }

    data class JobHeader(val job: String) : CastAndCrewListItem() {
        override fun getItemId(): String = "job_$job"
    }

    data class CastItem(val cast: ResponseCredit.Cast) : CastAndCrewListItem() {
        override fun getItemId(): String = "cast_${cast.id}"
    }

    data class CrewItem(val crew: ResponseCredit.Crew) : CastAndCrewListItem() {
        override fun getItemId(): String = "crew_${crew.id}"
    }

    abstract fun getItemId(): String
}
