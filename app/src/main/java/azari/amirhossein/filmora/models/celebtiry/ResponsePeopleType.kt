package azari.amirhossein.filmora.models.celebtiry

sealed class ResponsePeopleType{
    data class Trending(val result: ResponseTrendingCelebrity.Result) : ResponsePeopleType()
    data class Popular(val result: ResponsePopularCelebrity.Result) : ResponsePeopleType()
}