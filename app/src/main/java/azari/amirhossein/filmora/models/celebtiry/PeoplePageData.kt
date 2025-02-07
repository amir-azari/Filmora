package azari.amirhossein.filmora.models.celebtiry

import azari.amirhossein.filmora.utils.NetworkRequest

class PeoplePageData (
    val popular : NetworkRequest<ResponsePopularCelebrity>,
    val trending : NetworkRequest<ResponseTrendingCelebrity>
)